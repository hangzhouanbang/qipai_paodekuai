package com.anbang.qipai.paodekuai.web.vo;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameInfoDbo;

public class GameInfoVO {
	private String gameId;
	private int panNo;
	private int actionNo;
	private boolean bichu;

	public GameInfoVO() {

	}

	public GameInfoVO(PukeGameInfoDbo gameInfo) {
		gameId = gameInfo.getGameId();
		panNo = gameInfo.getPanNo();
		actionNo = gameInfo.getActionNo();
		bichu = gameInfo.isBichu();
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
