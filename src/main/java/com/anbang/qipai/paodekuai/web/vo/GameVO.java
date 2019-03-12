package com.anbang.qipai.paodekuai.web.vo;

import java.util.ArrayList;
import java.util.List;

import com.anbang.qipai.paodekuai.cqrs.c.domain.wanfa.ChaPai;
import com.anbang.qipai.paodekuai.cqrs.c.domain.wanfa.FaPai;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameDbo;
import com.dml.mpgame.game.Canceled;
import com.dml.mpgame.game.Finished;
import com.dml.mpgame.game.Playing;
import com.dml.mpgame.game.WaitingStart;
import com.dml.mpgame.game.extend.fpmpv.VoteNotPassWhenWaitingNextPan;
import com.dml.mpgame.game.extend.fpmpv.VotingWhenWaitingNextPan;
import com.dml.mpgame.game.extend.multipan.WaitingNextPan;
import com.dml.mpgame.game.extend.vote.FinishedByVote;
import com.dml.mpgame.game.extend.vote.VoteNotPassWhenPlaying;
import com.dml.mpgame.game.extend.vote.VotingWhenPlaying;
import com.dml.paodekuai.wanfa.OptionalPlay;

public class GameVO {
	private String id;// 就是gameid
	private int panshu;
	private int renshu;
	private OptionalPlay optionalPlay;
	// TODO: 2019/3/11  
	private boolean chaodi;
	private boolean shuangming;
	private boolean fengding;
	private ChaPai chapai;
	private FaPai fapai;
	private int panNo;
	private List<PukeGamePlayerVO> playerList;
	private String state;

	public GameVO(PukeGameDbo pukeGameDbo) {
		id = pukeGameDbo.getId();
		panshu = pukeGameDbo.getPanshu();
		renshu = pukeGameDbo.getRenshu();
		optionalPlay = pukeGameDbo.getOptionalPlay();
		chaodi = pukeGameDbo.isChaodi();
		shuangming = pukeGameDbo.isShuangming();
		fengding = pukeGameDbo.isFengding();
		chapai = pukeGameDbo.getChapai();
		fapai = pukeGameDbo.getFapai();
		playerList = new ArrayList<>();
		pukeGameDbo.getPlayers().forEach((dbo) -> playerList.add(new PukeGamePlayerVO(dbo)));
		panNo = pukeGameDbo.getPanNo();
		String sn = pukeGameDbo.getState().name();
		if (sn.equals(Canceled.name)) {
			state = "canceled";
		} else if (sn.equals(Finished.name)) {
			state = "finished";
		} else if (sn.equals(FinishedByVote.name)) {
			state = "finishedbyvote";
		} else if (sn.equals(Playing.name)) {
			state = "playing";
		} else if (sn.equals(VotingWhenPlaying.name)) {
			state = "playing";
		} else if (sn.equals(VoteNotPassWhenPlaying.name)) {
			state = "playing";
		} else if (sn.equals(VotingWhenWaitingNextPan.name)) {
			state = "waitingNextPan";
		} else if (sn.equals(VoteNotPassWhenWaitingNextPan.name)) {
			state = "waitingNextPan";
		} else if (sn.equals(WaitingNextPan.name)) {
			state = "waitingNextPan";
		} else if (sn.equals(WaitingStart.name)) {
			state = "waitingStart";
		} else {
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPanshu() {
		return panshu;
	}

	public void setPanshu(int panshu) {
		this.panshu = panshu;
	}

	public int getRenshu() {
		return renshu;
	}

	public void setRenshu(int renshu) {
		this.renshu = renshu;
	}

	public OptionalPlay getOptionalPlay() {
		return optionalPlay;
	}

	public void setOptionalPlay(OptionalPlay optionalPlay) {
		this.optionalPlay = optionalPlay;
	}

	public boolean isChaodi() {
		return chaodi;
	}

	public void setChaodi(boolean chaodi) {
		this.chaodi = chaodi;
	}

	public boolean isShuangming() {
		return shuangming;
	}

	public void setShuangming(boolean shuangming) {
		this.shuangming = shuangming;
	}

	public boolean isFengding() {
		return fengding;
	}

	public void setFengding(boolean fengding) {
		this.fengding = fengding;
	}

	public ChaPai getChapai() {
		return chapai;
	}

	public void setChapai(ChaPai chapai) {
		this.chapai = chapai;
	}

	public FaPai getFapai() {
		return fapai;
	}

	public void setFapai(FaPai fapai) {
		this.fapai = fapai;
	}

	public int getPanNo() {
		return panNo;
	}

	public void setPanNo(int panNo) {
		this.panNo = panNo;
	}

	public List<PukeGamePlayerVO> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<PukeGamePlayerVO> playerList) {
		this.playerList = playerList;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
