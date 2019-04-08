package com.anbang.qipai.paodekuai.cqrs.q.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anbang.qipai.paodekuai.cqrs.c.domain.GameInfoPlayerViewFilter;
import com.anbang.qipai.paodekuai.cqrs.c.domain.PanActionFramePlayerViewFilter;
import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PukeActionResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.ReadyForGameResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.ReadyToNextPanResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiPanResult;
import com.anbang.qipai.paodekuai.cqrs.q.dao.GameLatestPanActionFrameDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.GameLatestPukeGameInfoDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.JuResultDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.PanActionFrameDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.PanResultDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.PukeGameDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.PukeGameInfoDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.GameLatestPanActionFrameDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.GameLatestPukeGameInfoDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.JuResultDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PanActionFrameDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PanResultDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameInfoDbo;
import com.anbang.qipai.paodekuai.plan.bean.PlayerInfo;
import com.anbang.qipai.paodekuai.plan.dao.PlayerInfoDao;
import com.dml.mpgame.game.Playing;
import com.dml.mpgame.game.extend.vote.VoteNotPassWhenPlaying;
import com.dml.mpgame.game.extend.vote.VotingWhenPlaying;
import com.dml.paodekuai.pan.PanActionFrame;

@Service
public class PukePlayQueryService {

	@Autowired
	private PukeGameDboDao pukeGameDboDao;

	@Autowired
	private PlayerInfoDao playerInfoDao;

	@Autowired
	private PanResultDboDao panResultDboDao;

	@Autowired
	private JuResultDboDao juResultDboDao;

	@Autowired
	private PanActionFrameDboDao panActionFrameDboDao;

	@Autowired
	private PukeGameInfoDboDao pukeGameInfoDboDao;

	@Autowired
	private GameLatestPanActionFrameDboDao gameLatestPanActionFrameDboDao;

	@Autowired
	private GameLatestPukeGameInfoDboDao gameLatestPukeGameInfoDboDao;

	private PanActionFramePlayerViewFilter pvFilter = new PanActionFramePlayerViewFilter();

	private GameInfoPlayerViewFilter gvFilter = new GameInfoPlayerViewFilter();

	public PanActionFrame findAndFilterCurrentPanValueObjectForPlayer(String gameId, String playerId) throws Exception {
		PukeGameDbo pukeGameDbo = pukeGameDboDao.findById(gameId);
		if (!(pukeGameDbo.getState().name().equals(Playing.name)
				|| pukeGameDbo.getState().name().equals(VotingWhenPlaying.name)
				|| pukeGameDbo.getState().name().equals(VoteNotPassWhenPlaying.name))) {
			throw new Exception("game not playing");
		}

		GameLatestPanActionFrameDbo frame = gameLatestPanActionFrameDboDao.findById(gameId);
		PanActionFrame panActionFrame = pvFilter.filter(frame, playerId);
		return panActionFrame;
	}

	public PukeGameInfoDbo findAndFilterCurrentGameInfoForPlayer(String gameId, String playerId) throws Exception {
		PukeGameDbo pukeGameDbo = pukeGameDboDao.findById(gameId);
		if (!(pukeGameDbo.getState().name().equals(Playing.name)
				|| pukeGameDbo.getState().name().equals(VotingWhenPlaying.name)
				|| pukeGameDbo.getState().name().equals(VoteNotPassWhenPlaying.name))) {
			throw new Exception("game not playing");
		}

		GameLatestPukeGameInfoDbo info = gameLatestPukeGameInfoDboDao.findById(gameId);
		PukeGameInfoDbo pukeGameInfoDbo = gvFilter.filter(playerId, info);
		return pukeGameInfoDbo;
	}

	public void readyForGame(ReadyForGameResult readyForGameResult) throws Throwable {
		PukeGameValueObject pukeGame = readyForGameResult.getPukeGame();
		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		pukeGame.allPlayerIds().forEach((playerId) -> playerInfoMap.put(playerId, playerInfoDao.findById(playerId)));
		PukeGameDbo pukeGameDbo = new PukeGameDbo(pukeGame, playerInfoMap);
		pukeGameDboDao.save(pukeGameDbo);


		if (readyForGameResult.getFirstActionFrame() != null) {
			PanActionFrame panActionFrame = readyForGameResult.getFirstActionFrame();
			gameLatestPanActionFrameDboDao.save(pukeGame.getId(), panActionFrame);
			// 记录一条Frame，回放的时候要做
			String gameId = pukeGame.getId();
			int panNo = panActionFrame.getPanAfterAction().getNo();
			int actionNo = panActionFrame.getNo();
			PanActionFrameDbo panActionFrameDbo = new PanActionFrameDbo(gameId, panNo, actionNo);
			panActionFrameDbo.setPanActionFrame(panActionFrame);
			panActionFrameDboDao.save(panActionFrameDbo);

			PukeGameInfoDbo pukeGameInfoDbo = new PukeGameInfoDbo(pukeGame, playerInfoMap, actionNo, true);
			gameLatestPukeGameInfoDboDao.save(gameId, pukeGameInfoDbo);
			pukeGameInfoDboDao.save(pukeGameInfoDbo);
		}
	}

	public void action(PukeActionResult pukeActionResult) throws Throwable {

		PukeGameValueObject pukeGame = pukeActionResult.getPukeGame();
		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		pukeGame.allPlayerIds().forEach((playerId) -> playerInfoMap.put(playerId, playerInfoDao.findById(playerId)));
		PukeGameDbo pukeGameDbo = new PukeGameDbo(pukeGame, playerInfoMap);
		pukeGameDboDao.save(pukeGameDbo);

		String gameId = pukeGameDbo.getId();
		PanActionFrame panActionFrame = pukeActionResult.getPanActionFrame();
		gameLatestPanActionFrameDboDao.save(gameId, panActionFrame);
		// 记录一条Frame，回放的时候要做
		int panNo = panActionFrame.getPanAfterAction().getNo();
		int actionNo = panActionFrame.getNo();
		PanActionFrameDbo panActionFrameDbo = new PanActionFrameDbo(gameId, panNo, actionNo);
		panActionFrameDbo.setPanActionFrame(panActionFrame);
		panActionFrameDboDao.save(panActionFrameDbo);

		PukeGameInfoDbo pukeGameInfoDbo = new PukeGameInfoDbo(pukeGame, playerInfoMap, actionNo, false);
		gameLatestPukeGameInfoDboDao.save(gameId, pukeGameInfoDbo);
		pukeGameInfoDboDao.save(pukeGameInfoDbo);
		// 盘出结果的话要记录结果
		PaodekuaiPanResult paodekuaiPanResult = pukeActionResult.getPanResult();
		if (paodekuaiPanResult != null) {
			PanResultDbo panResultDbo = new PanResultDbo(gameId, paodekuaiPanResult);
			panResultDbo.setPanActionFrame(panActionFrame);
			panResultDboDao.save(panResultDbo);
			if (pukeActionResult.getJuResult() != null) {// 一切都结束了
				// 要记录局结果
				JuResultDbo juResultDbo = new JuResultDbo(gameId, panResultDbo, pukeActionResult.getJuResult());
				juResultDboDao.save(juResultDbo);
			}
		}
	}

	public void readyToNextPan(ReadyToNextPanResult readyToNextPanResult) throws Throwable {

		PukeGameValueObject pukeGame = readyToNextPanResult.getPukeGame();
		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		pukeGame.allPlayerIds().forEach((pid) -> playerInfoMap.put(pid, playerInfoDao.findById(pid)));
		PukeGameDbo pukeGameDbo = new PukeGameDbo(pukeGame, playerInfoMap);
		pukeGameDboDao.save(pukeGameDbo);

		if (readyToNextPanResult.getFirstActionFrame() != null) {
			String gameId = pukeGameDbo.getId();
			PanActionFrame panActionFrame = readyToNextPanResult.getFirstActionFrame();
			gameLatestPanActionFrameDboDao.save(gameId, panActionFrame);
			// 记录一条Frame，回放的时候要做
			int panNo = readyToNextPanResult.getFirstActionFrame().getPanAfterAction().getNo();
			int actionNo = readyToNextPanResult.getFirstActionFrame().getNo();
			PanActionFrameDbo panActionFrameDbo = new PanActionFrameDbo(gameId, panNo, actionNo);
			panActionFrameDbo.setPanActionFrame(readyToNextPanResult.getFirstActionFrame());
			panActionFrameDboDao.save(panActionFrameDbo);

			PukeGameInfoDbo pukeGameInfoDbo = new PukeGameInfoDbo(pukeGame, playerInfoMap, actionNo, true);
			gameLatestPukeGameInfoDboDao.save(gameId, pukeGameInfoDbo);
			pukeGameInfoDboDao.save(pukeGameInfoDbo);
		}

	}

	public PanResultDbo findPanResultDbo(String gameId, int panNo) {
		return panResultDboDao.findByGameIdAndPanNo(gameId, panNo);
	}

	public JuResultDbo findJuResultDbo(String gameId) {
		return juResultDboDao.findByGameId(gameId);
	}

	public List<PanActionFrameDbo> findPanActionFrameDboForBackPlay(String gameId, int panNo) {
		return panActionFrameDboDao.findByGameIdAndPanNo(gameId, panNo);
	}

	public List<PukeGameInfoDbo> findGameInfoDboForBackPlay(String gameId, int panNo) {
		return pukeGameInfoDboDao.findByGameIdAndPanNo(gameId, panNo);
	}
}
