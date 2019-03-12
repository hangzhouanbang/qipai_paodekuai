package com.anbang.qipai.paodekuai.web.vo;

import java.util.ArrayList;
import java.util.List;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.PanResultDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.WenzhouShuangkouPanPlayerResultDbo;
import com.dml.paodekuai.player.PaodekuaiPlayerValueObject;

public class PanResultVO {

	private List<PaodekuaiPanPlayerResultVO> playerResultList;

	private boolean chaodi;

	private int panNo;

	private long finishTime;

	private PanActionFrameVO lastPanActionFrame;

	public PanResultVO() {

	}

	public PanResultVO(PanResultDbo panResultDbo, PukeGameDbo pukeGameDbo) {
		List<WenzhouShuangkouPanPlayerResultDbo> list = panResultDbo.getPlayerResultList();
		List<PaodekuaiPlayerValueObject> players = panResultDbo.getPanActionFrame().getPanAfterAction()
				.getShuangkouPlayerList();
		if (list != null) {
			playerResultList = new ArrayList<>(list.size());
			list.forEach((panPlayerResult) -> {
				PaodekuaiPlayerValueObject shuangkouPlayer = null;
				for (PaodekuaiPlayerValueObject player : players) {
					if (player.getId().equals(panPlayerResult.getPlayerId())) {
						shuangkouPlayer = player;
						break;
					}
				}
				playerResultList.add(new PaodekuaiPanPlayerResultVO(
						pukeGameDbo.findPlayer(panPlayerResult.getPlayerId()), panPlayerResult, shuangkouPlayer));
			});
		}
		chaodi = panResultDbo.isChaodi();
		panNo = panResultDbo.getPanNo();
		finishTime = panResultDbo.getFinishTime();
		lastPanActionFrame = new PanActionFrameVO(panResultDbo.getPanActionFrame());
	}

	public PanActionFrameVO getLastPanActionFrame() {
		return lastPanActionFrame;
	}

	public void setLastPanActionFrame(PanActionFrameVO lastPanActionFrame) {
		this.lastPanActionFrame = lastPanActionFrame;
	}

	public List<PaodekuaiPanPlayerResultVO> getPlayerResultList() {
		return playerResultList;
	}

	public void setPlayerResultList(List<PaodekuaiPanPlayerResultVO> playerResultList) {
		this.playerResultList = playerResultList;
	}

	public boolean isChaodi() {
		return chaodi;
	}

	public void setChaodi(boolean chaodi) {
		this.chaodi = chaodi;
	}

	public int getPanNo() {
		return panNo;
	}

	public void setPanNo(int panNo) {
		this.panNo = panNo;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

}
