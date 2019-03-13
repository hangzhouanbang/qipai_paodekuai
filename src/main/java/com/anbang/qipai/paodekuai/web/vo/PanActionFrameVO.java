package com.anbang.qipai.paodekuai.web.vo;

import com.dml.paodekuai.pan.PanActionFrame;
import com.dml.paodekuai.player.action.PaodekuaiPlayerAction;

public class PanActionFrameVO {
	private int no;
	private PaodekuaiPlayerAction action;
	private PanValueObjectVO panAfterAction;
	private long actionTime;

	public PanActionFrameVO() {

	}

	public PanActionFrameVO(PanActionFrame panActionFrame) {
		no = panActionFrame.getNo();
		action = panActionFrame.getAction();
		panAfterAction = new PanValueObjectVO(panActionFrame.getPanAfterAction());
		actionTime = panActionFrame.getActionTime();
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public PaodekuaiPlayerAction getAction() {
		return action;
	}

	public void setAction(PaodekuaiPlayerAction action) {
		this.action = action;
	}

	public PanValueObjectVO getPanAfterAction() {
		return panAfterAction;
	}

	public void setPanAfterAction(PanValueObjectVO panAfterAction) {
		this.panAfterAction = panAfterAction;
	}

	public long getActionTime() {
		return actionTime;
	}

	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}

}
