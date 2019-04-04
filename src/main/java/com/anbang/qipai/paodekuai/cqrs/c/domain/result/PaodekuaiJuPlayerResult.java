package com.anbang.qipai.paodekuai.cqrs.c.domain.result;

public class PaodekuaiJuPlayerResult {
	private String playerId;
	private int totalScore;

	private int danguanCount;
	private int shuangguanCount;
	private int boomCount;
	private int maxScore;


	public void increaseDanguanCount(){
		danguanCount ++;
	}

	public void increaseShuangguanCount() {
		shuangguanCount ++;
	}

	public void increaseBoomCount(int boomNum) {
		boomCount = boomCount + boomNum;
	}

	public void increaseTotalScore(int score) {
		totalScore = totalScore + score;
	}

	/**
	 *  ------set/get
	 */

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

	public int getDanguanCount() {
		return danguanCount;
	}

	public void setDanguanCount(int danguanCount) {
		this.danguanCount = danguanCount;
	}

	public int getShuangguanCount() {
		return shuangguanCount;
	}

	public void setShuangguanCount(int shuangguanCount) {
		this.shuangguanCount = shuangguanCount;
	}

	public int getBoomCount() {
		return boomCount;
	}

	public void setBoomCount(int boomCount) {
		this.boomCount = boomCount;
	}

	public int getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(int maxScore) {
		this.maxScore = maxScore;
	}
}
