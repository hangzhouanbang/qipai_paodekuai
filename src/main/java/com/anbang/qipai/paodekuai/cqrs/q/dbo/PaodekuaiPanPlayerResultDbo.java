package com.anbang.qipai.paodekuai.cqrs.q.dbo;

import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiPanPlayerResult;
import com.dml.paodekuai.player.PaodekuaiPlayerValueObject;

public class PaodekuaiPanPlayerResultDbo {

	private String playerId;
	private PaodekuaiPanPlayerResult playerResult;
	private PaodekuaiPlayerValueObject player;

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public PaodekuaiPanPlayerResult getPlayerResult() {
		return playerResult;
	}

	public void setPlayerResult(PaodekuaiPanPlayerResult playerResult) {
		this.playerResult = playerResult;
	}

	public PaodekuaiPlayerValueObject getPlayer() {
		return player;
	}

	public void setPlayer(PaodekuaiPlayerValueObject player) {
		this.player = player;
	}

}
