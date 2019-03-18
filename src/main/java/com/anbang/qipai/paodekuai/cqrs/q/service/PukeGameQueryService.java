package com.anbang.qipai.paodekuai.cqrs.q.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anbang.qipai.paodekuai.cqrs.q.dao.WatchRecordDao;
import com.dml.mpgame.game.watch.WatchRecord;
import com.dml.mpgame.game.watch.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiJuResult;
import com.anbang.qipai.paodekuai.cqrs.q.dao.GameFinishVoteDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.JuResultDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.PukeGameDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.GameFinishVoteDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.JuResultDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameDbo;
import com.anbang.qipai.paodekuai.plan.bean.PlayerInfo;
import com.anbang.qipai.paodekuai.plan.dao.PlayerInfoDao;
import com.dml.mpgame.game.extend.vote.GameFinishVoteValueObject;

@Service
public class PukeGameQueryService {

	@Autowired
	private PukeGameDboDao pukeGameDboDao;

	@Autowired
	private PlayerInfoDao playerInfoDao;

	@Autowired
	private GameFinishVoteDboDao gameFinishVoteDboDao;

	@Autowired
	private JuResultDboDao juResultDboDao;

	@Autowired
	private WatchRecordDao watchRecordDao;

	public PukeGameDbo findPukeGameDboById(String gameId) {
		return pukeGameDboDao.findById(gameId);
	}

	public void newPukeGame(PukeGameValueObject pukeGame) {

		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		pukeGame.allPlayerIds().forEach((playerId) -> playerInfoMap.put(playerId, playerInfoDao.findById(playerId)));
		PukeGameDbo pukeGameDbo = new PukeGameDbo(pukeGame, playerInfoMap);
		pukeGameDboDao.save(pukeGameDbo);

	}

	public void joinGame(PukeGameValueObject pukeGame) {
		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		pukeGame.allPlayerIds().forEach((playerId) -> playerInfoMap.put(playerId, playerInfoDao.findById(playerId)));
		PukeGameDbo pukeGameDbo = new PukeGameDbo(pukeGame, playerInfoMap);
		pukeGameDboDao.save(pukeGameDbo);
	}

	public void leaveGame(PukeGameValueObject pukeGame) {
		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		pukeGame.allPlayerIds().forEach((playerId) -> playerInfoMap.put(playerId, playerInfoDao.findById(playerId)));
		PukeGameDbo pukeGameDbo = new PukeGameDbo(pukeGame, playerInfoMap);
		pukeGameDboDao.save(pukeGameDbo);

		gameFinishVoteDboDao.removeGameFinishVoteDboByGameId(pukeGame.getId());
		GameFinishVoteValueObject gameFinishVoteValueObject = pukeGame.getVote();
		GameFinishVoteDbo gameFinishVoteDbo = new GameFinishVoteDbo();
		gameFinishVoteDbo.setVote(gameFinishVoteValueObject);
		gameFinishVoteDbo.setGameId(pukeGame.getId());
		gameFinishVoteDboDao.save(gameFinishVoteDbo);

		PaodekuaiJuResult paodekuaiJuResult = (PaodekuaiJuResult) pukeGame.getJuResult();
		if (paodekuaiJuResult != null) {
			JuResultDbo juResultDbo = new JuResultDbo(pukeGame.getId(), null, paodekuaiJuResult);
			juResultDboDao.save(juResultDbo);
		}
	}

	public void backToGame(String playerId, PukeGameValueObject pukeGameValueObject) {
		pukeGameDboDao.updatePlayerOnlineState(pukeGameValueObject.getId(), playerId,
				pukeGameValueObject.findPlayerOnlineState(playerId));
		GameFinishVoteValueObject gameFinishVoteValueObject = pukeGameValueObject.getVote();
		gameFinishVoteDboDao.update(pukeGameValueObject.getId(), gameFinishVoteValueObject);
	}

	public void finish(PukeGameValueObject pukeGameValueObject) {
		gameFinishVoteDboDao.removeGameFinishVoteDboByGameId(pukeGameValueObject.getId());
		GameFinishVoteValueObject gameFinishVoteValueObject = pukeGameValueObject.getVote();
		GameFinishVoteDbo gameFinishVoteDbo = new GameFinishVoteDbo();
		gameFinishVoteDbo.setVote(gameFinishVoteValueObject);
		gameFinishVoteDbo.setGameId(pukeGameValueObject.getId());
		gameFinishVoteDboDao.save(gameFinishVoteDbo);

		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		pukeGameValueObject.allPlayerIds()
				.forEach((playerId) -> playerInfoMap.put(playerId, playerInfoDao.findById(playerId)));
		PukeGameDbo pukeGameDbo = new PukeGameDbo(pukeGameValueObject, playerInfoMap);
		pukeGameDboDao.save(pukeGameDbo);

		PaodekuaiJuResult paodekuaiJuResult = (PaodekuaiJuResult) pukeGameValueObject
				.getJuResult();
		if (paodekuaiJuResult != null) {
			JuResultDbo juResultDbo = new JuResultDbo(pukeGameValueObject.getId(), null, paodekuaiJuResult);
			juResultDboDao.save(juResultDbo);
		}
	}

	public void voteToFinish(PukeGameValueObject pukeGameValueObject) {
		GameFinishVoteValueObject gameFinishVoteValueObject = pukeGameValueObject.getVote();
		gameFinishVoteDboDao.update(pukeGameValueObject.getId(), gameFinishVoteValueObject);

		Map<String, PlayerInfo> playerInfoMap = new HashMap<>();
		pukeGameValueObject.allPlayerIds()
				.forEach((playerId) -> playerInfoMap.put(playerId, playerInfoDao.findById(playerId)));
		PukeGameDbo pukeGameDbo = new PukeGameDbo(pukeGameValueObject, playerInfoMap);
		pukeGameDboDao.save(pukeGameDbo);

		PaodekuaiJuResult paodekuaiJuResult = (PaodekuaiJuResult) pukeGameValueObject
				.getJuResult();
		if (paodekuaiJuResult != null) {
			JuResultDbo juResultDbo = new JuResultDbo(pukeGameValueObject.getId(), null, paodekuaiJuResult);
			juResultDboDao.save(juResultDbo);
		}
	}

	public GameFinishVoteDbo findGameFinishVoteDbo(String gameId) {
		return gameFinishVoteDboDao.findByGameId(gameId);
	}

	public WatchRecord saveWatchRecord(String gameId, Watcher watcher){
		WatchRecord watchRecord = watchRecordDao.findByGameId(gameId);
		if (watchRecord == null) {
			WatchRecord record = new WatchRecord();
			List<Watcher> watchers = new ArrayList<>();
			watchers.add(watcher);

			record.setGameId(gameId);
			record.setWatchers(watchers);
			watchRecordDao.save(record);
			return record;
		}

		watchRecord.getWatchers().add(watcher);
		for (Watcher list : watchRecord.getWatchers()) {
			if (list.getId().equals(watcher.getId())){
				list.setState(watcher.getState());
				watchRecordDao.save(watchRecord);
				return watchRecord;
			}
		}

		watchRecord.getWatchers().add(watcher);
		watchRecordDao.save(watchRecord);
		return watchRecord;
	}

	public boolean findByPlayerId(String gameId, String playerId){
		if (watchRecordDao.findByPlayerId(gameId,playerId) != null) {
			return true;
		}
		return false;
	}
}
