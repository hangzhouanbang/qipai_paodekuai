package com.anbang.qipai.paodekuai.cqrs.q.dbo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.anbang.qipai.paodekuai.plan.bean.PlayerInfo;
import com.dml.mpgame.game.GamePlayerValueObject;

@Document
@CompoundIndexes({ @CompoundIndex(name = "gameId_1_panNo_1", def = "{'gameId': 1, 'panNo': 1}") })
public class PukeGameInfoDbo {
	private String id;
	private String gameId;
	private int panNo;
	private int actionNo;
	private List<PukeGamePlayerInfoDbo> playerInfos;

	public PukeGameInfoDbo() {
	}

	public PukeGameInfoDbo(PukeGameValueObject pukeGame, Map<String, PlayerInfo> playerInfoMap, int actionNo) {
		gameId = pukeGame.getId();
		panNo = pukeGame.getPanNo();
		this.actionNo = actionNo;
		playerInfos = new ArrayList<>();
		for (GamePlayerValueObject playerValueObject : pukeGame.getPlayers()) {
			String playerId = playerValueObject.getId();
			PukeGamePlayerInfoDbo playerInfoDbo = new PukeGamePlayerInfoDbo();
			playerInfoDbo.setPlayerId(playerId);
			playerInfos.add(playerInfoDbo);
		}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public int getPanNo() {
		return panNo;
	}

	public void setPanNo(int panNo) {
		this.panNo = panNo;
	}

	public int getActionNo() {
		return actionNo;
	}

	public void setActionNo(int actionNo) {
		this.actionNo = actionNo;
	}

	public List<PukeGamePlayerInfoDbo> getPlayerInfos() {
		return playerInfos;
	}

	public void setPlayerInfos(List<PukeGamePlayerInfoDbo> playerInfos) {
		this.playerInfos = playerInfos;
	}

}
