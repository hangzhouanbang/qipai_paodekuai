package com.anbang.qipai.paodekuai.cqrs.q.dbo;

public class GameLatestPukeGameInfoDbo {
	private String id;// 就是gameid
	private PukeGameInfoDbo pukeGameInfoDbo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PukeGameInfoDbo getPukeGameInfoDbo() {
		return pukeGameInfoDbo;
	}

	public void setPukeGameInfoDbo(PukeGameInfoDbo pukeGameInfoDbo) {
		this.pukeGameInfoDbo = pukeGameInfoDbo;
	}

}
