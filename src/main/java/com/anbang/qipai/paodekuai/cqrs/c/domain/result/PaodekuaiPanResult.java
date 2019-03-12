package com.anbang.qipai.paodekuai.cqrs.c.domain.result;

import java.util.List;

import com.dml.paodekuai.pan.PanResult;

public class PaodekuaiPanResult extends PanResult {
	private boolean chaodi;
	private List<PaodekuaiPanPlayerResult> panPlayerResultList;

	public boolean isChaodi() {
		return chaodi;
	}

	public void setChaodi(boolean chaodi) {
		this.chaodi = chaodi;
	}

	public List<PaodekuaiPanPlayerResult> getPanPlayerResultList() {
		return panPlayerResultList;
	}

	public void setPanPlayerResultList(List<PaodekuaiPanPlayerResult> panPlayerResultList) {
		this.panPlayerResultList = panPlayerResultList;
	}

}
