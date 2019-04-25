package com.anbang.qipai.paodekuai.cqrs.c.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dml.paodekuai.pai.dianshuzu.ABoomDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.DaiPaiZhaDanDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.DianShuZuCalculator;
import com.dml.paodekuai.pai.dianshuzu.PaiXing;
import com.dml.paodekuai.player.action.da.solution.DaPaiDianShuSolution;
import com.dml.paodekuai.player.action.da.solution.ZaDanYaPaiSolutionCalculator;
import com.dml.paodekuai.wanfa.OptionalPlay;
import com.dml.puke.pai.DianShu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DanGeZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.ZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.ZhadanComparator;

public class PaodekuaiZaDanYaPaiSolutionCalculator implements ZaDanYaPaiSolutionCalculator {
	private OptionalPlay optionalPlay;
	private ZhadanComparator zhadanComparator;

	@Override
	public Map<String, DaPaiDianShuSolution> calculate(DianShuZu beiYaDianShuZu, int[] dianShuAmountArray) {
		int[] dianShuAmount = dianShuAmountArray.clone();
		Map<String, DaPaiDianShuSolution> yaPaiSolutionCandidates = new HashMap<>();
		Set<DaPaiDianShuSolution> solutionSet = new HashSet<>();

		// aaa炸
		if (optionalPlay.isaBoom()) {
			List<ABoomDianShuZu> aBoomDianShuZus = DianShuZuCalculator.calculateABoomZhadanDianShuZu(dianShuAmount);
			for (ABoomDianShuZu aBoomDianShuZu : aBoomDianShuZus) {
				DaPaiDianShuSolution solution = new DaPaiDianShuSolution();
				solution.setDianShuZu(aBoomDianShuZu);
				DianShu[] dachuDianShuArray = { aBoomDianShuZu.getDianShu(), aBoomDianShuZu.getDianShu(),
						aBoomDianShuZu.getDianShu() };
				solution.setDachuDianShuArray(dachuDianShuArray);
				solution.calculateDianshuZuheIdx();
				solutionSet.add(solution);
			}
		}

		// 跑的快没有王牌
		calculateDaPaiDianShuSolutionWithoutWangDang(dianShuAmount, beiYaDianShuZu, solutionSet);

		solutionSet.forEach((solution) -> {
			DaPaiDianShuSolution daPaiDianShuSolution = yaPaiSolutionCandidates.get(solution.getDianshuZuheIdx());
			if (daPaiDianShuSolution != null) {
				DianShuZu dianShuZu = daPaiDianShuSolution.getDianShuZu();
				// 有可能出现打出点数相同类型却不同的情况
				if (!solution.getDianShuZu().getClass().equals(dianShuZu.getClass())) {

				} else if (dianShuZu instanceof ABoomDianShuZu) {
					yaPaiSolutionCandidates.put(solution.getDianshuZuheIdx(), solution);
				}
			} else {
				yaPaiSolutionCandidates.put(solution.getDianshuZuheIdx(), solution);
			}
		});
		return yaPaiSolutionCandidates;
	}

	private void calculateDaPaiDianShuSolutionWithoutWangDang(int[] dianshuCountArray, DianShuZu beiYaDianShuZu,
			Set<DaPaiDianShuSolution> solutionSet) {
		PaiXing paiXing = new PaiXing();
		// 普通炸弹
		paiXing.setDanGeZhadanDianShuZuList(DianShuZuCalculator.calculateDanGeZhadanDianShuZu(dianshuCountArray));
		paiXing.setDaipaiZhaDanDianShuZuList(DianShuZuCalculator.calculateDaiPaiZhaDanDianShuZu(dianshuCountArray));
		paiXing = paiXingFilter(paiXing, beiYaDianShuZu);
		solutionSet.addAll(DianShuZuCalculator.calculateAllDaPaiDianShuSolutionWithoutWangDang(paiXing));
	}

	private PaiXing paiXingFilter(PaiXing paiXing, DianShuZu beiYaDianShuZu) {
		PaiXing filtedPaiXing = new PaiXing();
		if (beiYaDianShuZu instanceof ZhadanDianShuZu) {
			if (beiYaDianShuZu instanceof DaiPaiZhaDanDianShuZu) {
				DaiPaiZhaDanDianShuZu beiYaZhadanDianShuZu = (DaiPaiZhaDanDianShuZu) beiYaDianShuZu;
				List<DanGeZhadanDianShuZu> filtedDanGeZhadanDianShuZuList = filtedPaiXing.getDanGeZhadanDianShuZuList();
				List<DanGeZhadanDianShuZu> zhadanDianShuZuList = paiXing.getDanGeZhadanDianShuZuList();
				for (DanGeZhadanDianShuZu danGeZhadanDianShuZu : zhadanDianShuZuList) {
					if (zhadanComparator.compare(danGeZhadanDianShuZu, beiYaZhadanDianShuZu) > 0) {
						filtedDanGeZhadanDianShuZuList.add(danGeZhadanDianShuZu);
					}
				}
				List<DaiPaiZhaDanDianShuZu> filtedDaipaiZhaDanDianShuZuList = filtedPaiXing
						.getDaipaiZhaDanDianShuZuList();
				List<DaiPaiZhaDanDianShuZu> daipaiZhaDanDianShuZuList = paiXing.getDaipaiZhaDanDianShuZuList();
				for (DaiPaiZhaDanDianShuZu daiPaiZhaDanDianShuZu : daipaiZhaDanDianShuZuList) {
					if (zhadanComparator.compare(daiPaiZhaDanDianShuZu, beiYaZhadanDianShuZu) > 0) {
						filtedDaipaiZhaDanDianShuZuList.add(daiPaiZhaDanDianShuZu);
					}
				}
			} else {
				ZhadanDianShuZu beiYaZhadanDianShuZu = (ZhadanDianShuZu) beiYaDianShuZu;
				List<DanGeZhadanDianShuZu> filtedDanGeZhadanDianShuZuList = filtedPaiXing.getDanGeZhadanDianShuZuList();
				List<DanGeZhadanDianShuZu> zhadanDianShuZuList = paiXing.getDanGeZhadanDianShuZuList();
				for (DanGeZhadanDianShuZu danGeZhadanDianShuZu : zhadanDianShuZuList) {
					if (zhadanComparator.compare(danGeZhadanDianShuZu, beiYaZhadanDianShuZu) > 0) {
						filtedDanGeZhadanDianShuZuList.add(danGeZhadanDianShuZu);
					}
				}
				List<DaiPaiZhaDanDianShuZu> filtedDaipaiZhaDanDianShuZuList = filtedPaiXing
						.getDaipaiZhaDanDianShuZuList();
				List<DaiPaiZhaDanDianShuZu> daipaiZhaDanDianShuZuList = paiXing.getDaipaiZhaDanDianShuZuList();
				for (DaiPaiZhaDanDianShuZu daiPaiZhaDanDianShuZu : daipaiZhaDanDianShuZuList) {
					if (zhadanComparator.compare(daiPaiZhaDanDianShuZu, beiYaZhadanDianShuZu) > 0) {
						filtedDaipaiZhaDanDianShuZuList.add(daiPaiZhaDanDianShuZu);
					}
				}
			}
		} else {
			filtedPaiXing.setDanGeZhadanDianShuZuList(paiXing.getDanGeZhadanDianShuZuList());
			filtedPaiXing.setDaipaiZhaDanDianShuZuList(paiXing.getDaipaiZhaDanDianShuZuList());
		}
		return filtedPaiXing;
	}

	public OptionalPlay getOptionalPlay() {
		return optionalPlay;
	}

	public void setOptionalPlay(OptionalPlay optionalPlay) {
		this.optionalPlay = optionalPlay;
	}

	public ZhadanComparator getZhadanComparator() {
		return zhadanComparator;
	}

	public void setZhadanComparator(ZhadanComparator zhadanComparator) {
		this.zhadanComparator = zhadanComparator;
	}

}
