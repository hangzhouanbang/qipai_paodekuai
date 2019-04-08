package com.anbang.qipai.paodekuai.cqrs.q.dbo;

import java.util.Map;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.anbang.qipai.paodekuai.plan.bean.PlayerInfo;

@Document
@CompoundIndexes({ @CompoundIndex(name = "gameId_1_panNo_1", def = "{'gameId': 1, 'panNo': 1}") })
public class PukeGameInfoDbo {
	private String id;
	private String gameId;
	private int panNo;
	private int actionNo;
	private boolean bichu;

	public PukeGameInfoDbo() {
	}

	public PukeGameInfoDbo(PukeGameValueObject pukeGame, Map<String, PlayerInfo> playerInfoMap, int actionNo, boolean start) {
		gameId = pukeGame.getId();
		panNo = pukeGame.getPanNo();
		this.actionNo = actionNo;

		// 新一盘游戏且选了必出玩法，给出必出提示
		if (start == true && pukeGame.getOptionalPlay().isBichu()) {
			bichu = true;
		} else {
			bichu = false;
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

	public boolean isBichu() {
		return bichu;
	}

	public void setBichu(boolean bichu) {
		this.bichu = bichu;
	}
}
