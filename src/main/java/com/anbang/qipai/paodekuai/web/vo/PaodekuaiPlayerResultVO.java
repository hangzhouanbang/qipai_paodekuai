package com.anbang.qipai.paodekuai.web.vo;

import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiJuPlayerResult;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGamePlayerDbo;

public class PaodekuaiPlayerResultVO {
	private String playerId;
	private String nickname;
	private String headimgurl;
	private int totalScore;

	private int danguanCount;
	private int shuangguanCount;
	private int boomCount;
	private int maxScore;

	public PaodekuaiPlayerResultVO(PukeGamePlayerDbo playerDbo) {
		playerId = playerDbo.getPlayerId();
		nickname = playerDbo.getNickname();
		headimgurl = playerDbo.getHeadimgurl();
		totalScore = 0;
	}

	public PaodekuaiPlayerResultVO(PaodekuaiJuPlayerResult juPlayerResult,
								   PukeGamePlayerDbo playerDbo) {
		playerId = playerDbo.getPlayerId();
		nickname = playerDbo.getNickname();
		headimgurl = playerDbo.getHeadimgurl();
		totalScore = juPlayerResult.getTotalScore();

		danguanCount = juPlayerResult.getDanguanCount();
		shuangguanCount = juPlayerResult.getShuangguanCount();
		boomCount = juPlayerResult.getBoomCount();
		maxScore = juPlayerResult.getMaxScore();
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
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
