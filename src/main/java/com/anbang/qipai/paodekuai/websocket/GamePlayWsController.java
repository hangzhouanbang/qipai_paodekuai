package com.anbang.qipai.paodekuai.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dml.mpgame.game.watch.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.anbang.qipai.paodekuai.cqrs.c.service.GameCmdService;
import com.anbang.qipai.paodekuai.cqrs.c.service.PlayerAuthService;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.JuResultDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameDbo;
import com.anbang.qipai.paodekuai.cqrs.q.service.PukeGameQueryService;
import com.anbang.qipai.paodekuai.cqrs.q.service.PukePlayQueryService;
import com.anbang.qipai.paodekuai.msg.msjobj.PukeHistoricalJuResult;
import com.anbang.qipai.paodekuai.msg.service.PaodekuaiGameMsgService;
import com.anbang.qipai.paodekuai.msg.service.PaodekuaiResultMsgService;
import com.dml.mpgame.game.Canceled;
import com.dml.mpgame.game.Finished;
import com.dml.mpgame.game.GameState;
import com.dml.mpgame.game.extend.vote.FinishedByVote;
import com.dml.mpgame.game.player.GamePlayerState;
import com.google.gson.Gson;

@Component
public class GamePlayWsController extends TextWebSocketHandler {
	@Autowired
	private GamePlayWsNotifier wsNotifier;

	@Autowired
	private PlayerAuthService playerAuthService;

	@Autowired
	private GameCmdService gameCmdService;

	@Autowired
	private PukeGameQueryService pukeGameQueryService;

	@Autowired
	private PukePlayQueryService pukePlayQueryService;

	@Autowired
	private PaodekuaiGameMsgService gameMsgService;

	@Autowired
	private PaodekuaiResultMsgService paodekuaiResultMsgService;

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private Gson gson = new Gson();

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		executorService.submit(() -> {
			CommonMO mo = gson.fromJson(message.getPayload(), CommonMO.class);
			String msg = mo.getMsg();
			if ("bindPlayer".equals(msg)) {// 绑定玩家
				processBindPlayer(session, mo.getData());
			}
			if ("heartbeat".equals(msg)) {// 心跳
				processHeartbeat(session, mo.getData());
			} else {
			}
		});

	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		wsNotifier.addSession(session);
		CommonMO mo = new CommonMO();
		mo.setMsg("bindPlayer");
		sendMessage(session, gson.toJson(mo));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String closedPlayerId = wsNotifier.findPlayerIdBySessionId(session.getId());
		wsNotifier.removeSession(session.getId());
		// 有可能断的只是一个已经废弃了的session，新的session已经建立。这个时候其实不是leave的
		if (wsNotifier.hasSessionForPlayer(closedPlayerId)) {
			return;
		}

		PukeGameValueObject pukeGameValueObject = gameCmdService.leaveGameByOffline(closedPlayerId);
		if (pukeGameValueObject != null) {
			pukeGameQueryService.leaveGame(pukeGameValueObject);
			gameMsgService.gamePlayerLeave(pukeGameValueObject, closedPlayerId);

			String gameId = pukeGameValueObject.getId();
			if (pukeGameValueObject.getState().name().equals(FinishedByVote.name)
					|| pukeGameValueObject.getState().name().equals(Canceled.name)
					|| pukeGameValueObject.getState().name().equals(Finished.name)) {
				JuResultDbo juResultDbo = pukePlayQueryService.findJuResultDbo(gameId);
				if (juResultDbo != null) {
					PukeGameDbo pukeGameDbo = pukeGameQueryService.findPukeGameDboById(gameId);
					PukeHistoricalJuResult juResult = new PukeHistoricalJuResult(juResultDbo, pukeGameDbo);
					paodekuaiResultMsgService.recordJuResult(juResult);
				}
				gameMsgService.gameFinished(gameId);
			}
			// 通知其他人
			for (String otherPlayerId : pukeGameValueObject.allPlayerIds()) {
				if (!otherPlayerId.equals(closedPlayerId)) {
					List<QueryScope> scopes = QueryScope.scopesForState(pukeGameValueObject.getState(),
							pukeGameValueObject.findPlayerState(otherPlayerId));
					scopes.remove(QueryScope.panResult);
					wsNotifier.notifyToQuery(otherPlayerId, scopes);
				}
			}
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable error) throws Exception {
		executorService.submit(() -> {
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		error.printStackTrace();
	}

	/**
	 * 绑定玩家
	 * 
	 * @param session
	 * @param data
	 */
	private void processBindPlayer(WebSocketSession session, Object data) {
		Map map = (Map) data;
		String token = (String) map.get("token");
		String gameId = (String) map.get("gameId");
		if (token == null) {// 非法访问
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String playerId = playerAuthService.getPlayerIdByToken(token);
		if (playerId == null) {// 非法的token
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		wsNotifier.bindPlayer(session.getId(), playerId);
		try {
			gameCmdService.bindPlayer(playerId, gameId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//查询观战信息
		Map<String, Watcher> watcherMap = gameCmdService.getwatch(gameId);
		if (!CollectionUtils.isEmpty(watcherMap) && watcherMap.containsKey(playerId)) {
			List<String> playerIds = new ArrayList<>();
			playerIds.add(playerId);
			wsNotifier.notifyToWatchQuery(playerIds,"bindPlayer");
			return;
		}

		// 给用户安排query scope
		PukeGameDbo pukeGameDbo = pukeGameQueryService.findPukeGameDboById(gameId);
		if (pukeGameDbo != null) {

			GameState gameState = pukeGameDbo.getState();

			//观战结束
			if (pukeGameQueryService.findByPlayerId(gameId,playerId) &&  gameState.name().equals(Finished.name)) {
				List<String> playerIds = new ArrayList<>();
				playerIds.add(playerId);
				wsNotifier.notifyToWatchQuery(playerIds, WatchQueryScope.watchEnd.name());
				return;
			}

			GamePlayerState playerState = pukeGameDbo.findPlayer(playerId).getState();

			List<QueryScope> scopes = QueryScope.scopesForState(gameState, playerState);
			wsNotifier.notifyToQuery(playerId, scopes);

		}
	}

	/**
	 * 心跳
	 *
	 * @param session
	 * @param data
	 */
	private void processHeartbeat(WebSocketSession session, Object data) {
		Map map = (Map) data;
		String token = (String) map.get("token");
		if (token == null) {// 非法访问
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String playerId = playerAuthService.getPlayerIdByToken(token);
		if (playerId == null) {// 非法的token
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		wsNotifier.updateSession(session.getId());
	}

	private void sendMessage(WebSocketSession session, String message) {
		synchronized (session) {
			try {
				session.sendMessage(new TextMessage(message));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
