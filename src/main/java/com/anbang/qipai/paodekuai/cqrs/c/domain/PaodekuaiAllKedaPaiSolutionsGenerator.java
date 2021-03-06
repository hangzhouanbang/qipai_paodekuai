package com.anbang.qipai.paodekuai.cqrs.c.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.dml.paodekuai.pai.dianshuzu.ABoomDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.DianShuZuCalculator;
import com.dml.paodekuai.pai.dianshuzu.PaiXing;
import com.dml.paodekuai.pai.dianshuzu.PaodekuaiDianShuZuGenerator;
import com.dml.paodekuai.pai.dianshuzu.comparator.DaipaiComparator;
import com.dml.paodekuai.player.action.da.AllKedaPaiSolutionsGenerator;
import com.dml.paodekuai.player.action.da.solution.DaPaiDianShuSolution;
import com.dml.paodekuai.wanfa.OptionalPlay;
import com.dml.puke.pai.DianShu;
import com.dml.puke.pai.PukePai;
import com.dml.puke.pai.PukePaiMian;
import com.dml.puke.wanfa.dianshu.dianshuzu.DanzhangDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZuGenerator;
import com.dml.puke.wanfa.dianshu.dianshuzu.ZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.LianXuDianShuZuComparator;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.ZhadanComparator;

public class PaodekuaiAllKedaPaiSolutionsGenerator implements AllKedaPaiSolutionsGenerator {
	private OptionalPlay optionalPlay;

	private ZhadanComparator zhadanComparator;

	private LianXuDianShuZuComparator lianXuDianShuZuComparator;

	private DaipaiComparator daipaiComparator;

	@Override
	public Map<String, DaPaiDianShuSolution> generateAllKedaPaiSolutions(Map<Integer, PukePai> allShoupai,
			boolean baodan) {
		Map<String, DaPaiDianShuSolution> yaPaiSolutionCandidates = new HashMap<>();
		Set<DaPaiDianShuSolution> solutionSet = new HashSet<>();

		// 所有手牌的点数数量数组
		int[] dianshuCountArray = new int[15];
		for (PukePai pukePai : allShoupai.values()) {
			DianShu dianShu = pukePai.getPaiMian().dianShu();
			dianshuCountArray[dianShu.ordinal()]++;
		}

		// 可打的单张牌
		if (baodan) {
			List<DanzhangDianShuZu> danzhangDianShuZuList = PaodekuaiDianShuZuGenerator
					.largestDanzhangDianshuzu(dianshuCountArray);
			solutionSet.addAll(DianShuZuCalculator.generateAllDanzhangDaPaiDianShuSolution(danzhangDianShuZuList));
		} else {
			List<DanzhangDianShuZu> danzhangDianShuZuList = DianShuZuGenerator
					.generateAllDanzhangDianShuZu(dianshuCountArray);
			solutionSet.addAll(DianShuZuCalculator.generateAllDanzhangDaPaiDianShuSolution(danzhangDianShuZuList));
		}

		// aaa炸
		if (optionalPlay.isaBoom()) {
			List<ABoomDianShuZu> aBoomDianShuZus = DianShuZuCalculator.calculateABoomZhadanDianShuZu(dianshuCountArray);
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
		// 其他牌型的出牌方案
		calculateDaPaiDianShuSolutionWithoutWangDang(dianshuCountArray, solutionSet, allShoupai.size());

		solutionSet.forEach((solution) -> {
			DaPaiDianShuSolution daPaiDianShuSolution = yaPaiSolutionCandidates.get(solution.getDianshuZuheIdx());
			if (solution.getDianShuZu() instanceof ZhadanDianShuZu) {// 炸弹最大
				yaPaiSolutionCandidates.put(solution.getDianshuZuheIdx(), solution);
			} else {
				if (daPaiDianShuSolution == null) {
					yaPaiSolutionCandidates.put(solution.getDianshuZuheIdx(), solution);
				}
			}
		});

		return yaPaiSolutionCandidates;
	}

	@Override
	public Map<String, DaPaiDianShuSolution> firstAllKedaPaiSolutions(Map<Integer, PukePai> allShoupai) {
		// 不是首张必出黑桃三即为一般方法
		if (!optionalPlay.isBichu()) {
			return generateAllKedaPaiSolutions(allShoupai, false);
		}

		// 黑桃三必出玩法
		Map<String, DaPaiDianShuSolution> yaPaiSolutionCandidates = new HashMap<>();
		Map<String, DaPaiDianShuSolution> allPaiSolutionCandidates = generateAllKedaPaiSolutions(allShoupai, false);
		allPaiSolutionCandidates.forEach((k, v) -> {
			if (ArrayUtils.contains(v.getDachuDianShuArray(), DianShu.san)) {
				v.getBichuPai().add(PukePaiMian.heitaosan);
				yaPaiSolutionCandidates.put(k, v);
			}
		});
		return yaPaiSolutionCandidates;
	}

	private void calculateDaPaiDianShuSolutionWithoutWangDang(int[] dianshuCountArray,
			Set<DaPaiDianShuSolution> solutionSet, int shoupaiCount) {
		PaiXing paiXing = new PaiXing();
		// 对子
		paiXing.setDuiziDianShuZuList(DianShuZuCalculator.calculateDuiziDianShuZu(dianshuCountArray));
		// 顺子
		paiXing.setShunziDianShuZuList(DianShuZuCalculator.calculateShunziDianShuZu(dianshuCountArray));
		// 连对
		paiXing.setLianduiDianShuZuList(DianShuZuCalculator.calculateLianduiDianShuZu(dianshuCountArray));
		// 三张
		paiXing.setSanzhangDianShuList(DianShuZuCalculator.calculateSanzhangDianShuZu(dianshuCountArray));
		// 四带二
		paiXing.setSidaierDianShuZulist(DianShuZuCalculator.calculateSidaierDianShuZu(dianshuCountArray));
		// 四带三
		paiXing.setSidaisanDianShuZuList(DianShuZuCalculator.calculateSidaisanDianShuZu(dianshuCountArray));
		// 三带二
		paiXing.setSandaierDianShuZuArrayList(DianShuZuCalculator.calculateSandaierDianShuZu(dianshuCountArray,
				shoupaiCount, optionalPlay.isSandaique()));
		// 飞机
		paiXing.setFeijiDianShuZuArrayList(DianShuZuCalculator.calculateFeijiDianShuZu(dianshuCountArray, shoupaiCount,
				optionalPlay.isFeijique()));
		// 普通炸弹
		paiXing.setDanGeZhadanDianShuZuList(DianShuZuCalculator.calculateDanGeZhadanDianShuZu(dianshuCountArray));
		// 带牌炸弹（四带一）
		paiXing.setDaipaiZhaDanDianShuZuList((DianShuZuCalculator.calculateDaiPaiZhaDanDianShuZu(dianshuCountArray)));

		solutionSet.addAll(DianShuZuCalculator.calculateAllDaPaiDianShuSolutionWithoutWangDang(paiXing));
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

	public LianXuDianShuZuComparator getLianXuDianShuZuComparator() {
		return lianXuDianShuZuComparator;
	}

	public void setLianXuDianShuZuComparator(LianXuDianShuZuComparator lianXuDianShuZuComparator) {
		this.lianXuDianShuZuComparator = lianXuDianShuZuComparator;
	}

	public DaipaiComparator getDaipaiComparator() {
		return daipaiComparator;
	}

	public void setDaipaiComparator(DaipaiComparator daipaiComparator) {
		this.daipaiComparator = daipaiComparator;
	}
}
