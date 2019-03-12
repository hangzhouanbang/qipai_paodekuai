package com.anbang.qipai.paodekuai.msg.msjobj;

import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiJuPlayerResult;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGamePlayerDbo;

public class PaodekuaiJuPlayerResultMO {
	private String playerId;
	private String nickname;
	private String headimgurl;
//	private int shuangkouCount;
//	private int dankouCount;
//	private int pingkouCount;
//	private int maxXianshu;
	private int totalScore;

	public PaodekuaiJuPlayerResultMO(PukeGamePlayerDbo playerDbo) {
		playerId = playerDbo.getPlayerId();
		nickname = playerDbo.getNickname();
		headimgurl = playerDbo.getHeadimgurl();
		totalScore = 0;
	}

	public PaodekuaiJuPlayerResultMO(PaodekuaiJuPlayerResult juPlayerResult,
									 PukeGamePlayerDbo playerDbo) {
		playerId = playerDbo.getPlayerId();
		nickname = playerDbo.getNickname();
		headimgurl = playerDbo.getHeadimgurl();
		totalScore = juPlayerResult.getTotalScore();
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

}
