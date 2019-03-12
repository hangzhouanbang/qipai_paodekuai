package com.anbang.qipai.paodekuai.web.vo;

import java.util.ArrayList;
import java.util.List;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameInfoDbo;

public class GameInfoVO {
	private String gameId;
	private int panNo;
	private int actionNo;
	private List<PukeGamePlayerInfoVO> playerInfos;
//	private List<String> chaodiPlayerIdList;

	public GameInfoVO() {

	}

	public GameInfoVO(PukeGameInfoDbo gameInfo) {
		gameId = gameInfo.getGameId();
		panNo = gameInfo.getPanNo();
		actionNo = gameInfo.getActionNo();
		playerInfos = new ArrayList<>();
		gameInfo.getPlayerInfos().forEach((playerInfo) -> playerInfos.add(new PukeGamePlayerInfoVO(playerInfo)));
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

	public List<PukeGamePlayerInfoVO> getPlayerInfos() {
		return playerInfos;
	}

	public void setPlayerInfos(List<PukeGamePlayerInfoVO> playerInfos) {
		this.playerInfos = playerInfos;
	}

}
