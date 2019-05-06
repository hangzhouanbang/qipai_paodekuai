package com.anbang.qipai.paodekuai.msg.receiver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.anbang.qipai.paodekuai.cqrs.c.service.GameCmdService;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.JuResultDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGamePlayerDbo;
import com.anbang.qipai.paodekuai.cqrs.q.service.PukeGameQueryService;
import com.anbang.qipai.paodekuai.cqrs.q.service.PukePlayQueryService;
import com.anbang.qipai.paodekuai.msg.channel.GameRoomSink;
import com.anbang.qipai.paodekuai.msg.msjobj.CommonMO;
import com.anbang.qipai.paodekuai.msg.msjobj.PukeHistoricalJuResult;
import com.anbang.qipai.paodekuai.msg.service.PaodekuaiGameMsgService;
import com.anbang.qipai.paodekuai.msg.service.PaodekuaiResultMsgService;
import com.dml.mpgame.game.player.GamePlayerOnlineState;
import com.google.gson.Gson;

@EnableBinding(GameRoomSink.class)
public class GameRoomMsgReceiver {

	@Autowired
	private GameCmdService gameCmdService;

	@Autowired
	private PukeGameQueryService pukeGameQueryService;

	@Autowired
	private PukePlayQueryService pukePlayQueryService;

	@Autowired
	private PaodekuaiResultMsgService paodekuaiResultMsgService;

	@Autowired
	private PaodekuaiGameMsgService paodekuaiGameMsgService;

	private Gson gson = new Gson();

	@StreamListener(GameRoomSink.PAODEKUAIGAMEROOM)
	public void removeGameRoom(CommonMO mo) {
		String msg = mo.getMsg();
		String json = gson.toJson(mo.getData());
		if ("gameIds".equals(msg)) {
			List<String> gameIds = gson.fromJson(json, ArrayList.class);
			for (String gameId : gameIds) {
				try {
					if (StringUtil.isBlank(gameId)) {
						continue;
					}
					PukeGameDbo pukeGameDbo = pukeGameQueryService.findPukeGameDboById(gameId);
					if (pukeGameDbo == null) {
						continue;
					}
					boolean playerOnline = false;
					for (PukeGamePlayerDbo player : pukeGameDbo.getPlayers()) {
						if (GamePlayerOnlineState.online.equals(player.getOnlineState())) {
							playerOnline = true;
						}
					}
					if (playerOnline) {
						paodekuaiGameMsgService.delay(gameId);
					} else {
						PukeGameValueObject gameValueObject = gameCmdService.finishGameImmediately(gameId);
						pukeGameQueryService.finishGameImmediately(gameValueObject);
						paodekuaiGameMsgService.gameFinished(gameId);
						JuResultDbo juResultDbo = pukePlayQueryService.findJuResultDbo(gameId);
						if (juResultDbo != null) {
							PukeHistoricalJuResult juResult = new PukeHistoricalJuResult(juResultDbo, pukeGameDbo);
							paodekuaiResultMsgService.recordJuResult(juResult);
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

}
