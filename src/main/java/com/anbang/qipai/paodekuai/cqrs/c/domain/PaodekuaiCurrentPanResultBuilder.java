package com.anbang.qipai.paodekuai.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.anbang.qipai.paodekuai.constants.PaodekuaiConstant;
import com.anbang.qipai.paodekuai.cqrs.c.domain.listener.BoomCountDaActionStatisticsListener;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiPanPlayerResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiPanResult;
import com.dml.paodekuai.ju.Ju;
import com.dml.paodekuai.pan.CurrentPanResultBuilder;
import com.dml.paodekuai.pan.Pan;
import com.dml.paodekuai.pan.PanResult;
import com.dml.paodekuai.pan.PanValueObject;
import com.dml.paodekuai.player.PaodekuaiPlayer;
import com.dml.paodekuai.wanfa.OptionalPlay;

public class PaodekuaiCurrentPanResultBuilder implements CurrentPanResultBuilder {
	private int renshu;
	private OptionalPlay optionalPlay;

	@Override
	public PanResult buildCurrentPanResult(Ju ju, long panFinishTime) {
		Pan currentPan = ju.getCurrentPan();

		//玩家之前盘的得分
		PaodekuaiPanResult latestFinishedPanResult = (PaodekuaiPanResult) ju
				.findLatestFinishedPanResult();
		Map<String, Integer> playerTotalScoreMap = new HashMap<>();
		if (latestFinishedPanResult != null) {
			for (PaodekuaiPanPlayerResult panPlayerResult : latestFinishedPanResult.getPanPlayerResultList()) {
				playerTotalScoreMap.put(panPlayerResult.getPlayerId(), panPlayerResult.getTotalScore());
			}
		} else {
			for (String list : currentPan.findAllPlayerId()) {
				playerTotalScoreMap.put(list, 0);
			}
		}

		//玩家炸弹数
		BoomCountDaActionStatisticsListener paodekuaiListener = ju.getActionStatisticsListenerManager()
				.findDaListener(BoomCountDaActionStatisticsListener.class);
		Map<String, Integer> boomMap = paodekuaiListener.getPlayerzhadanshuMap();

//		List<String> noPaiPlayerIdList = currentPan.getNoPaiPlayerIdList();
		List<String> playerIds = currentPan.findAllPlayerId();
		String zhuaniaoPlayer = currentPan.getZhuaniaoPlayerId();

		int playPaiCount = PaodekuaiConstant.PAI_COUNT / 3;

		List<PaodekuaiPanPlayerResult> panPlayerResultList = new ArrayList<>();

		// 赢家
		String winner = currentPan.getNoPaiPlayerIdList().get(0);
		boolean winnerZhuaniao = winner.equals(zhuaniaoPlayer);
		int winnerScore = 0;
		int winnerBoomScore = boomMap.get(winner) * PaodekuaiConstant.BOOM_SCORE;
		int guanmenCount = 0;

		// 输家
		List<String> loserList = playerIds.stream().filter(p -> !p.equals(winner)).collect(Collectors.toList());
		for (String loser: loserList) {
			PaodekuaiPanPlayerResult loserPanResulet = new PaodekuaiPanPlayerResult();
			loserPanResulet.setPlayerId(loser);

			int paiScore = currentPan.getPaodekuaiPlayerIdMajiangPlayerMap().get(loser).getAllShoupai().size();
			loserPanResulet.setYupaiCount(paiScore);

			if (paiScore == 1) {	// 报单不扣牌分
				paiScore = 0;
				loserPanResulet.setBaodan(true);
			}

			if (paiScore == playPaiCount) {		//关门手牌分翻倍
				paiScore = paiScore * 2;
				loserPanResulet.setGuanmen(true);
				guanmenCount ++;
			}

			if (optionalPlay.isZhuaniao() && (winnerZhuaniao || loser.equals(zhuaniaoPlayer))) {	//抓鸟翻倍
				paiScore = paiScore * 2;
				if (loser.equals(zhuaniaoPlayer)) {
					loserPanResulet.setZhuaniao(true);
				}
			}

			if (boomMap.get(loser) != null) {	// 输家可能一张牌都没出
				int loserBoomCount = boomMap.get(loser);
				loserPanResulet.setZhadanCount(loserBoomCount);
			}

			int loserScore = paiScore + winnerBoomScore;
			loserPanResulet.setScore(- loserScore);
			loserPanResulet.setTotalScore(- loserScore + playerTotalScoreMap.get(loser));
			panPlayerResultList.add(loserPanResulet);

			winnerScore = winnerScore + loserScore;
		}

		PaodekuaiPanPlayerResult winnerPanResulet = new PaodekuaiPanPlayerResult();
		winnerPanResulet.setPlayerId(winner);
		winnerPanResulet.setZhadanCount(boomMap.get(winner));
		winnerPanResulet.setZhuaniao(winnerZhuaniao);
		winnerPanResulet.setScore(winnerScore);
		winnerPanResulet.setTotalScore(winnerScore + playerTotalScoreMap.get(winner));
		winnerPanResulet.setWin(true);
		winnerPanResulet.setGuanmenCount(guanmenCount);
		panPlayerResultList.add(winnerPanResulet);

		PaodekuaiPanResult paodekuaiPanResult = new PaodekuaiPanResult();
		paodekuaiPanResult.setPan(new PanValueObject(currentPan));
		paodekuaiPanResult.setPanFinishTime(panFinishTime);
		paodekuaiPanResult.setPanPlayerResultList(panPlayerResultList);
		return paodekuaiPanResult;

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

}
