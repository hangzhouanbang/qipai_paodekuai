package com.anbang.qipai.paodekuai.cqrs.c.domain.result;

public class PaodekuaiPanPlayerResult {
	private String playerId;
	private int zhadanCount; // 炸弹数
	private boolean baodan; // 报单
	private boolean guanmen; // 关门
	private boolean zhuaniao; //抓鸟
	private int score;// 一盘结算分
	private int totalScore;// 总分

	private boolean win;
	private int guanmenCount;  // 赢家关门几人
	private int yupaiCount;  // 剩余手牌数

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public int getZhadanCount() {
		return zhadanCount;
	}

	public void setZhadanCount(int zhadanCount) {
		this.zhadanCount = zhadanCount;
	}

	public boolean isBaodan() {
		return baodan;
	}

	public void setBaodan(boolean baodan) {
		this.baodan = baodan;
	}

	public boolean isGuanmen() {
		return guanmen;
	}

	public void setGuanmen(boolean guanmen) {
		this.guanmen = guanmen;
	}

	public boolean isZhuaniao() {
		return zhuaniao;
	}

	public void setZhuaniao(boolean zhuaniao) {
		this.zhuaniao = zhuaniao;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public boolean isWin() {
		return win;
	}

	public void setWin(boolean win) {
		this.win = win;
	}

	public int getGuanmenCount() {
		return guanmenCount;
	}

	public void setGuanmenCount(int guanmenCount) {
		this.guanmenCount = guanmenCount;
	}

	public int getYupaiCount() {
		return yupaiCount;
	}

	public void setYupaiCount(int yupaiCount) {
		this.yupaiCount = yupaiCount;
	}
}
