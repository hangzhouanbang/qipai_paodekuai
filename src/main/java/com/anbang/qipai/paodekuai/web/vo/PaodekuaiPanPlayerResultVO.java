package com.anbang.qipai.paodekuai.web.vo;

import java.util.List;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGamePlayerDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.WenzhouShuangkouPanPlayerResultDbo;
import com.dml.paodekuai.player.PaodekuaiPlayerValueObject;

public class PaodekuaiPanPlayerResultVO {
	private String playerId;
	private String nickname;
	private String headimgurl;
	private ShuangkouPlayerShoupaiVO allShoupai;
//	private int xianshubeishu;
//	private int gongxianfen;
//	private int bufen;
	private int score;// 一盘结算分
	private int totalScore;// 总分

	public PaodekuaiPanPlayerResultVO() {

	}

	public PaodekuaiPanPlayerResultVO(PukeGamePlayerDbo playerDbo,
									  WenzhouShuangkouPanPlayerResultDbo panPlayerResult, PaodekuaiPlayerValueObject shuangkouPlayerValueObject) {
		playerId = playerDbo.getPlayerId();
		nickname = playerDbo.getNickname();
		headimgurl = playerDbo.getHeadimgurl();
		List<List<Integer>> shoupaiIdListForSortList = shuangkouPlayerValueObject.getShoupaiIdListForSortList();
		if (shoupaiIdListForSortList == null || shoupaiIdListForSortList.isEmpty()) {
			allShoupai = new ShuangkouPlayerShoupaiVO(shuangkouPlayerValueObject.getAllShoupai(),
					shuangkouPlayerValueObject.getTotalShoupai(), null);
		} else {
			allShoupai = new ShuangkouPlayerShoupaiVO(shuangkouPlayerValueObject.getAllShoupai(),
					shuangkouPlayerValueObject.getTotalShoupai(), shoupaiIdListForSortList.get(0));
		}
		score = panPlayerResult.getPlayerResult().getScore();
		totalScore = panPlayerResult.getPlayerResult().getTotalScore();
	}

	public ShuangkouPlayerShoupaiVO getAllShoupai() {
		return allShoupai;
	}

	public void setAllShoupai(ShuangkouPlayerShoupaiVO allShoupai) {
		this.allShoupai = allShoupai;
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

}
