package com.anbang.qipai.paodekuai.cqrs.c.domain.result;

public class PaodekuaiJuPlayerResult {
	private String playerId;
	private int totalScore;

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
}
