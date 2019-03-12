package com.anbang.qipai.paodekuai.cqrs.c.domain.result;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.dml.paodekuai.pan.PanActionFrame;

public class ReadyForGameResult {
	private PukeGameValueObject pukeGame;
	private PanActionFrame firstActionFrame;

	public PukeGameValueObject getPukeGame() {
		return pukeGame;
	}

	public void setPukeGame(PukeGameValueObject pukeGame) {
		this.pukeGame = pukeGame;
	}

	public PanActionFrame getFirstActionFrame() {
		return firstActionFrame;
	}

	public void setFirstActionFrame(PanActionFrame firstActionFrame) {
		this.firstActionFrame = firstActionFrame;
	}

}
