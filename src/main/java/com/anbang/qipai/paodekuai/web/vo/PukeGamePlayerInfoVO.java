package com.anbang.qipai.paodekuai.web.vo;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGamePlayerInfoDbo;

public class PukeGamePlayerInfoVO {
	private String playerId;
//	private int gongxianfen;
//	private int maxXianshu;
//	private int otherMaxXianshu;
//	private int totalGongxianfen;
	private boolean nopai;
//	private int mingci;

	public PukeGamePlayerInfoVO() {

	}

	public PukeGamePlayerInfoVO(PukeGamePlayerInfoDbo dbo) {
		playerId = dbo.getPlayerId();
		nopai = dbo.isNopai();
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public boolean isNopai() {
		return nopai;
	}

	public void setNopai(boolean nopai) {
		this.nopai = nopai;
	}

}
