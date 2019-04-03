package com.anbang.qipai.paodekuai.cqrs.c.service;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.ReadyForGameResult;
import com.dml.mpgame.game.GameValueObject;
import com.dml.paodekuai.wanfa.OptionalPlay;

import java.util.Map;

public interface GameCmdService {

	/**
	 * 新一局
	 */
	PukeGameValueObject newPukeGame(String gameId, String playerId, Integer panshu, Integer renshu, OptionalPlay optionalPlay);

	PukeGameValueObject newPukeGameLeaveAndQuit(String gameId, String playerId, Integer panshu, Integer renshu, OptionalPlay optionalPlay);

	PukeGameValueObject joinGame(String playerId, String gameId) throws Exception;

	PukeGameValueObject leaveGame(String playerId) throws Exception;

	PukeGameValueObject leaveGameByHangup(String playerId) throws Exception;

	PukeGameValueObject leaveGameByOffline(String playerId) throws Exception;

	PukeGameValueObject backToGame(String playerId, String gameId) throws Exception;

	ReadyForGameResult readyForGame(String playerId, Long currentTime) throws Exception;

	ReadyForGameResult cancelReadyForGame(String playerId, Long currentTime) throws Exception;

	PukeGameValueObject finish(String playerId, Long currentTime) throws Exception;

	PukeGameValueObject voteToFinish(String playerId, Boolean yes) throws Exception;

	PukeGameValueObject voteToFinishByTimeOver(String playerId, Long currentTime) throws Exception;

	PukeGameValueObject finishGameImmediately(String gameId) throws Exception;

	void bindPlayer(String playerId, String gameId) throws Exception;

	PukeGameValueObject joinWatch (String playerId, String nickName, String headimgurl, String gameId) throws Exception;

	PukeGameValueObject leaveWatch (String playerId, String gameId) throws Exception;

	Map getwatch (String gameId);

	void recycleWatch(String gameId);
}
