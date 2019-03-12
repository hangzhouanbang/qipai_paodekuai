package com.anbang.qipai.paodekuai.cqrs.c.domain.result;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.dml.paodekuai.pan.PanActionFrame;

public class PukeActionResult {
	private PukeGameValueObject pukeGame;
	private PanActionFrame panActionFrame;
	private PaodekuaiPanResult panResult;
	private PaodekuaiJuResult juResult;

	public PukeGameValueObject getPukeGame() {
		return pukeGame;
	}

	public void setPukeGame(PukeGameValueObject pukeGame) {
		this.pukeGame = pukeGame;
	}

	public PanActionFrame getPanActionFrame() {
		return panActionFrame;
	}

	public void setPanActionFrame(PanActionFrame panActionFrame) {
		this.panActionFrame = panActionFrame;
	}

	public PaodekuaiPanResult getPanResult() {
		return panResult;
	}

	public void setPanResult(PaodekuaiPanResult panResult) {
		this.panResult = panResult;
	}

	public PaodekuaiJuResult getJuResult() {
		return juResult;
	}

	public void setJuResult(PaodekuaiJuResult juResult) {
		this.juResult = juResult;
	}

}
