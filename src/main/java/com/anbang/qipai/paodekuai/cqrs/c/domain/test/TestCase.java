package com.anbang.qipai.paodekuai.cqrs.c.domain.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PaodekuaiZhadanComparator;
import com.dml.paodekuai.pai.dianshuzu.ABoomDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.DaiPaiZhaDanDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.DianShuZuCalculator;
import com.dml.paodekuai.pai.dianshuzu.FeijiDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.PaiXing;
import com.dml.paodekuai.pai.dianshuzu.PaodekuaiDianShuZuGenerator;
import com.dml.paodekuai.pai.dianshuzu.SandaierDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.SidaierDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.SidaisanDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.comparator.DaipaiComparator;
import com.dml.paodekuai.pai.dianshuzu.comparator.TongDengDaiPaiComparator;
import com.dml.paodekuai.player.action.da.solution.DaPaiDianShuSolution;
import com.dml.paodekuai.wanfa.OptionalPlay;
import com.dml.puke.pai.DianShu;
import com.dml.puke.pai.PukePaiMian;
import com.dml.puke.wanfa.dianshu.dianshuzu.DanGeZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DanzhangDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZuGenerator;
import com.dml.puke.wanfa.dianshu.dianshuzu.DuiziDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.LianduiDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.SanzhangDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.ShunziDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.ZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.LianXuDianShuZuComparator;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.TongDengLianXuDianShuZuComparator;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.ZhadanComparator;

public class TestCase {
	private static OptionalPlay optionalPlay = new OptionalPlay();;

	private static ZhadanComparator zhadanComparator = new PaodekuaiZhadanComparator();

	private static LianXuDianShuZuComparator lianXuDianShuZuComparator = new TongDengLianXuDianShuZuComparator();

	private static DaipaiComparator daipaiComparator = new TongDengDaiPaiComparator();

	public static void main(String[] args) {
		optionalPlay.setShowShoupaiNum(true);
		Map<String, DaPaiDianShuSolution> yaPaiSolutionCandidates = generateAllKedaPaiSolutions(false);
		List<DaPaiDianShuSolution> YaPaiSolutions = new ArrayList<>(yaPaiSolutionCandidates.values());
		List<DaPaiDianShuSolution> filtedSolutionList = filter(YaPaiSolutions, false);
		System.out.println("-----");
	}

	public static Map<String, DaPaiDianShuSolution> generateAllKedaPaiSolutions(boolean baodan) {
		Map<String, DaPaiDianShuSolution> yaPaiSolutionCandidates = new HashMap<>();
		Set<DaPaiDianShuSolution> solutionSet = new HashSet<>();

		// 所有手牌的点数数量数组
		int[] dianshuCountArray = { 3, 3, 3, 1, 0, 2, 1, 1, 0, 0, 1, 1, 0, 0, 0 };

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
		calculateDaPaiDianShuSolutionWithoutWangDang(dianshuCountArray, solutionSet, 13);

		solutionSet.forEach((solution) -> {
			DaPaiDianShuSolution daPaiDianShuSolution = yaPaiSolutionCandidates.get(solution.getDianshuZuheIdx());
			// TODO: 2019/3/12
			// 跑的快这里不考虑癞子玩法时会出现点数相同类型不同的情况
			yaPaiSolutionCandidates.put(solution.getDianshuZuheIdx(), solution);
		});

		return yaPaiSolutionCandidates;
	}

	public static Map<String, DaPaiDianShuSolution> firstAllKedaPaiSolutions() {
		// 不是首张必出黑桃三即为一般方法
		if (!optionalPlay.isBichu()) {
			return generateAllKedaPaiSolutions(false);
		}

		// 黑桃三必出玩法
		Map<String, DaPaiDianShuSolution> yaPaiSolutionCandidates = new HashMap<>();
		Map<String, DaPaiDianShuSolution> allPaiSolutionCandidates = generateAllKedaPaiSolutions(false);
		allPaiSolutionCandidates.forEach((k, v) -> {
			if (ArrayUtils.contains(v.getDachuDianShuArray(), DianShu.san)) {
				v.getBichuPai().add(PukePaiMian.heitaosan);
				yaPaiSolutionCandidates.put(k, v);
			}
		});
		return yaPaiSolutionCandidates;
	}

	private static void calculateDaPaiDianShuSolutionWithoutWangDang(int[] dianshuCountArray,
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

	public static List<DaPaiDianShuSolution> filter(List<DaPaiDianShuSolution> YaPaiSolutions, boolean yapai) {
		List<DaPaiDianShuSolution> filtedSolutionList = new LinkedList<>();
		List<DaPaiDianShuSolution> noLaiziSolutionList = new ArrayList<>(YaPaiSolutions);

		// 手牌转为点数数量数组
		int[] dianshuCountArray = { 3, 3, 3, 1, 0, 2, 1, 1, 0, 0, 1, 1, 0, 0, 0 };

		// 取出可打牌中点数的最大炸弹，并排序（实际上对于跑的快，只需排序最大点数长度炸弹即可）
		LinkedList<DaPaiDianShuSolution> danGeZhadanSolutionList = new LinkedList<>();
		for (DaPaiDianShuSolution solution : noLaiziSolutionList) {
			DianShuZu dianshuZu = solution.getDianShuZu();
			if (dianshuZu instanceof DanGeZhadanDianShuZu) {
				if (dianshuZu instanceof DaiPaiZhaDanDianShuZu) {
					DaiPaiZhaDanDianShuZu danGeZhadanDianShuZu1 = (DaiPaiZhaDanDianShuZu) dianshuZu;
					DianShu dianshu = danGeZhadanDianShuZu1.getZhadanDian();
					if (danGeZhadanSolutionList.isEmpty()) {
						danGeZhadanSolutionList.add(solution);
					} else {
						int length = danGeZhadanSolutionList.size();
						for (int i = 0; i < length; i++) {
							DanGeZhadanDianShuZu danGeZhadanDianShuZu2 = (DanGeZhadanDianShuZu) danGeZhadanSolutionList
									.get(i).getDianShuZu();
							DianShu ds = danGeZhadanDianShuZu2.getDianShu();
							if (danGeZhadanDianShuZu2 instanceof DaiPaiZhaDanDianShuZu) {
								DaiPaiZhaDanDianShuZu danGeZhadanDianShuZu3 = (DaiPaiZhaDanDianShuZu) danGeZhadanDianShuZu2;
								ds = danGeZhadanDianShuZu3.getZhadanDian();
							}
							int compare = ds.compareTo(dianshu);
							if (compare > 0) {
								danGeZhadanSolutionList.add(i, solution);
								break;
							}
							if (i == length - 1) {
								danGeZhadanSolutionList.add(solution);
							}
						}
					}
				} else {
					DanGeZhadanDianShuZu danGeZhadanDianShuZu1 = (DanGeZhadanDianShuZu) dianshuZu;
					DianShu dianshu = danGeZhadanDianShuZu1.getDianShu();
					if (danGeZhadanDianShuZu1.getSize() == dianshuCountArray[dianshu.ordinal()]) {// 炸弹长度==该点数最大长度
						if (danGeZhadanSolutionList.isEmpty()) {
							danGeZhadanSolutionList.add(solution);
						} else {
							int length = danGeZhadanSolutionList.size();
							for (int i = 0; i < length; i++) {
								DanGeZhadanDianShuZu danGeZhadanDianShuZu2 = (DanGeZhadanDianShuZu) danGeZhadanSolutionList
										.get(i).getDianShuZu();
								int compare = danGeZhadanDianShuZu2.getDianShu().compareTo(dianshu);
								if (compare > 0) {
									danGeZhadanSolutionList.add(i, solution);
									break;
								}
								if (i == length - 1) {
									danGeZhadanSolutionList.add(solution);
								}
							}
						}
					}
				}
			}
		}

		LinkedList<DaPaiDianShuSolution> noWangDanzhangSolutionList = new LinkedList<>();
		LinkedList<DaPaiDianShuSolution> noWangDuiziSolutionList = new LinkedList<>();
		LinkedList<DaPaiDianShuSolution> noWangSandaierSolutionList = new LinkedList<>();
		LinkedList<DaPaiDianShuSolution> noWangShunziSolutionList = new LinkedList<>();
		LinkedList<DaPaiDianShuSolution> noWangLianduiSolutionList = new LinkedList<>();
		LinkedList<DaPaiDianShuSolution> noWangFeijiSolutionList = new LinkedList<>();
		LinkedList<DaPaiDianShuSolution> sanzhangSolutionList = new LinkedList<>();
		LinkedList<DaPaiDianShuSolution> sidaierSolutionList = new LinkedList<>();
		LinkedList<DaPaiDianShuSolution> sidaisanSolutionList = new LinkedList<>();

		// 单张至两张的单张
		LinkedList<DaPaiDianShuSolution> noWangDanzhangYiShangSolutionList = new LinkedList<>();
		// 两张以上的单张
		LinkedList<DaPaiDianShuSolution> noWangDanzhangDuiziYiShangSolutionList = new LinkedList<>();
		// 有三张的对子
		LinkedList<DaPaiDianShuSolution> noWangDuiziYiShangSolutionList = new LinkedList<>();

		if (!yapai) {
			for (DaPaiDianShuSolution solution : noLaiziSolutionList) {
				DianShuZu dianshuZu = solution.getDianShuZu();
				// 三张
				if (dianshuZu instanceof SanzhangDianShuZu) {
					SanzhangDianShuZu sanzhangDianShuZu = (SanzhangDianShuZu) dianshuZu;
					DianShu dianshu = sanzhangDianShuZu.getDianShu();
					if (dianshuCountArray[dianshu.ordinal()] < 4) {
						if (sanzhangSolutionList.isEmpty()) {
							sanzhangSolutionList.add(solution);
						} else {
							int length = sanzhangSolutionList.size();
							int i = 0;
							while (i < length) {
								if (((SanzhangDianShuZu) sanzhangSolutionList.get(i).getDianShuZu()).getDianShu()
										.compareTo(sanzhangDianShuZu.getDianShu()) > 0) {
									sanzhangSolutionList.add(i, solution);
									break;
								}
								if (i == length - 1) {
									sanzhangSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}

				// 四带二
				if (dianshuZu instanceof SidaierDianShuZu) {
					SidaierDianShuZu sidaierDianShuZu = (SidaierDianShuZu) dianshuZu;
					DianShu dianShu = sidaierDianShuZu.getDanpaiDianShu();
					DianShu[] dianpaiArray = sidaierDianShuZu.getDaipaiDianShuArray();

					// TODO: 2019/3/15 根据被带牌与原牌的关系挑选提示牌
					if (sidaierSolutionList.isEmpty()) {
						sidaierSolutionList.add(solution);
					} else {
						int length = sidaierSolutionList.size();
						int i = 0;
						while (i < length) {
							SidaierDianShuZu tempDianshuzu = (SidaierDianShuZu) sidaierSolutionList.get(i)
									.getDianShuZu();
							if (tempDianshuzu.getDanpaiDianShu().compareTo(dianShu) > 0) {
								sidaierSolutionList.add(i, solution);
								break;
							}
							if (i == length - 1) {
								noWangSandaierSolutionList.add(solution);
							}
							i++;
						}
					}
				}
				// 四带三
				if (dianshuZu instanceof SidaisanDianShuZu) {
					final SidaisanDianShuZu sidaisanDianShuZu = (SidaisanDianShuZu) dianshuZu;
					DianShu dianShu = sidaisanDianShuZu.getDanpaiDianShu();
					DianShu[] dianpaiArray = sidaisanDianShuZu.getDaipaiDianShuArray();

					if (sidaierSolutionList.isEmpty()) {
						sidaierSolutionList.add(solution);
					} else {
						int length = sidaierSolutionList.size();
						int i = 0;
						while (i < length) {
							SidaierDianShuZu tempDianshuzu = (SidaierDianShuZu) sidaierSolutionList.get(i)
									.getDianShuZu();
							if (tempDianshuzu.getDanpaiDianShu().compareTo(dianShu) > 0) {
								sidaierSolutionList.add(i, solution);
								break;
							}
							if (i == length - 1) {
								noWangSandaierSolutionList.add(solution);
							}
							i++;
						}
					}
				}

				// 三带二
				if (dianshuZu instanceof SandaierDianShuZu) {
					SandaierDianShuZu sandaierDianShuZu = (SandaierDianShuZu) dianshuZu;
					DianShu dianshu = sandaierDianShuZu.getSanzhangDianShuArray()[0];
					// DianShu[] daipai = sandaierDianShuZu.getDaipaiDianShuArray();
					if (dianshuCountArray[dianshu.ordinal()] == 3) { // 取只有三张牌的牌组
						if (noWangSandaierSolutionList.isEmpty()) {
							noWangSandaierSolutionList.add(solution);
						} else {
							int length = noWangSandaierSolutionList.size();
							int i = 0;
							while (i < length) {
								SandaierDianShuZu tempDianshuzu = (SandaierDianShuZu) noWangSandaierSolutionList.get(i)
										.getDianShuZu();
								if (tempDianshuzu.getSanzhangDianShuArray()[0]
										.compareTo(sandaierDianShuZu.getSanzhangDianShuArray()[0]) > 0) {
									noWangSandaierSolutionList.add(i, solution);
									break;
								}

								if (i == length - 1) {
									noWangSandaierSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}
				// 对子
				if (dianshuZu instanceof DuiziDianShuZu) {
					DuiziDianShuZu duiziDianShuZu = (DuiziDianShuZu) dianshuZu;
					DianShu dianshu = duiziDianShuZu.getDianShu();
					if (dianshuCountArray[dianshu.ordinal()] == 2) {
						if (noWangDuiziSolutionList.isEmpty()) {
							noWangDuiziSolutionList.add(solution);
						} else {
							int length = noWangDuiziSolutionList.size();
							int i = 0;
							while (i < length) {
								if (((DuiziDianShuZu) noWangDuiziSolutionList.get(i).getDianShuZu()).getDianShu()
										.compareTo(duiziDianShuZu.getDianShu()) > 0) {
									noWangDuiziSolutionList.add(i, solution);
									break;
								}
								if (i == length - 1) {
									noWangDuiziSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}
				// 单张
				if (dianshuZu instanceof DanzhangDianShuZu) {
					DanzhangDianShuZu danzhangDianShuZu = (DanzhangDianShuZu) dianshuZu;
					DianShu dianshu = danzhangDianShuZu.getDianShu();
					if (dianshuCountArray[dianshu.ordinal()] == 1) {
						if (noWangDanzhangSolutionList.isEmpty()) {
							noWangDanzhangSolutionList.add(solution);
						} else {
							int length = noWangDanzhangSolutionList.size();
							int i = 0;
							while (i < length) {
								if (((DanzhangDianShuZu) noWangDanzhangSolutionList.get(i).getDianShuZu()).getDianShu()
										.compareTo(danzhangDianShuZu.getDianShu()) > 0) {
									noWangDanzhangSolutionList.add(i, solution);
									break;
								}
								if (i == length - 1) {
									noWangDanzhangSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}
				// 飞机
				if (dianshuZu instanceof FeijiDianShuZu) {
					FeijiDianShuZu feijiDianShuZu = (FeijiDianShuZu) dianshuZu;
					DianShu[] sanzhangArray = feijiDianShuZu.getSanzhangDianShuArray();
					DianShu[] daipaiArray = feijiDianShuZu.getDaipaiDianShuArray();

					// 不拆炸弹
					boolean boomFlag = false;
					for (int i = 0; i < sanzhangArray.length; i++) {
						int dianshuIndex = sanzhangArray[i].ordinal();
						if (dianshuCountArray[dianshuIndex] != 3) {
							boomFlag = true;
						}
					}
					if (!boomFlag) {
						if (noWangFeijiSolutionList.isEmpty()) {
							noWangFeijiSolutionList.add(solution);
						} else {
							int length = noWangFeijiSolutionList.size();
							int i = 0;
							while (i < length) {
								FeijiDianShuZu tempFeiji = (FeijiDianShuZu) noWangFeijiSolutionList.get(i)
										.getDianShuZu();
								// 不同类型飞机直接加入，同类型比大小
								if (sanzhangArray.length != tempFeiji.getDaipaiDianShuArray().length) {
									noWangFeijiSolutionList.add(solution);
									break;
								} else {
									if (tempFeiji.getSanzhangDianShuArray()[0].compareTo(sanzhangArray[0]) > 0) {
										noWangFeijiSolutionList.add(i, solution);
										break;
									}
									if (i == length - 1) {
										noWangFeijiSolutionList.add(solution);
									}
								}
								i++;
							}
						}
					}
				}
				// 连对
				if (dianshuZu instanceof LianduiDianShuZu) {
					LianduiDianShuZu lianduiDianShuZu = (LianduiDianShuZu) dianshuZu;
					DianShu[] lianXuDianShuArray = lianduiDianShuZu.getLianXuDianShuArray();
					boolean allSuccess = true;
					// for (DianShu dianshu : lianXuDianShuArray) {
					// if (dianshuCountArray[dianshu.ordinal()] != 2) {
					// allSuccess = false;
					// break;
					// }
					// }
					if (allSuccess) {
						if (noWangLianduiSolutionList.isEmpty()) {
							noWangLianduiSolutionList.add(solution);
						} else {
							int length = noWangLianduiSolutionList.size();
							int i = 0;
							while (i < length) {
								if (((LianduiDianShuZu) noWangLianduiSolutionList.get(i).getDianShuZu())
										.getLianXuDianShuArray()[0].compareTo(
												((LianduiDianShuZu) dianshuZu).getLianXuDianShuArray()[0]) > 0) {
									noWangLianduiSolutionList.add(i, solution);
									break;
								}
								if (i == length - 1) {
									noWangLianduiSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}
				// 顺子
				if (dianshuZu instanceof ShunziDianShuZu) {
					ShunziDianShuZu shunziDianShuZu = (ShunziDianShuZu) dianshuZu;
					DianShu[] lianXuDianShuArray = shunziDianShuZu.getLianXuDianShuArray();
					boolean allSuccess = true;
					// for (DianShu dianshu : lianXuDianShuArray) {
					// if (dianshuCountArray[dianshu.ordinal()] != 1) {
					// allSuccess = false;
					// break;
					// }
					// }
					if (allSuccess) {
						if (noWangShunziSolutionList.isEmpty()) {
							noWangShunziSolutionList.add(solution);
						} else {
							int length = noWangShunziSolutionList.size();
							int i = 0;
							while (i < length) {
								if (((ShunziDianShuZu) noWangShunziSolutionList.get(i).getDianShuZu())
										.getLianXuDianShuArray()[0].compareTo(
												((ShunziDianShuZu) dianshuZu).getLianXuDianShuArray()[0]) > 0) {
									noWangShunziSolutionList.add(i, solution);
									break;
								}
								if (i == length - 1) {
									noWangShunziSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}
			}
		} else { // 压牌时的提示
			for (DaPaiDianShuSolution solution : noLaiziSolutionList) {
				DianShuZu dianshuZu = solution.getDianShuZu();
				// 三张
				if (dianshuZu instanceof SanzhangDianShuZu) {
					SanzhangDianShuZu sanzhangDianShuZu = (SanzhangDianShuZu) dianshuZu;
					DianShu dianshu = sanzhangDianShuZu.getDianShu();
					if (dianshuCountArray[dianshu.ordinal()] < 4) {
						if (sanzhangSolutionList.isEmpty()) {
							sanzhangSolutionList.add(solution);
						} else {
							int length = sanzhangSolutionList.size();
							int i = 0;
							while (i < length) {
								if (((SanzhangDianShuZu) sanzhangSolutionList.get(i).getDianShuZu()).getDianShu()
										.compareTo(sanzhangDianShuZu.getDianShu()) > 0) {
									sanzhangSolutionList.add(i, solution);
									break;
								}
								if (i == length - 1) {
									sanzhangSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}

				// 四带二
				if (dianshuZu instanceof SidaierDianShuZu) {
					SidaierDianShuZu sidaierDianShuZu = (SidaierDianShuZu) dianshuZu;
					DianShu dianShu = sidaierDianShuZu.getDanpaiDianShu();
					DianShu[] dianpaiArray = sidaierDianShuZu.getDaipaiDianShuArray();

					// TODO: 2019/3/15 根据被带牌与原牌的关系挑选提示牌
					if (sidaierSolutionList.isEmpty()) {
						sidaierSolutionList.add(solution);
					} else {
						int length = sidaierSolutionList.size();
						int i = 0;
						while (i < length) {
							SidaierDianShuZu tempDianshuzu = (SidaierDianShuZu) sidaierSolutionList.get(i)
									.getDianShuZu();
							if (tempDianshuzu.getDanpaiDianShu().compareTo(dianShu) > 0) {
								sidaierSolutionList.add(i, solution);
								break;
							}
							if (i == length - 1) {
								noWangSandaierSolutionList.add(solution);
							}
							i++;
						}
					}
				}
				// 四带三
				if (dianshuZu instanceof SidaisanDianShuZu) {
					final SidaisanDianShuZu sidaisanDianShuZu = (SidaisanDianShuZu) dianshuZu;
					DianShu dianShu = sidaisanDianShuZu.getDanpaiDianShu();
					DianShu[] dianpaiArray = sidaisanDianShuZu.getDaipaiDianShuArray();

					if (sidaierSolutionList.isEmpty()) {
						sidaierSolutionList.add(solution);
					} else {
						int length = sidaierSolutionList.size();
						int i = 0;
						while (i < length) {
							SidaierDianShuZu tempDianshuzu = (SidaierDianShuZu) sidaierSolutionList.get(i)
									.getDianShuZu();
							if (tempDianshuzu.getDanpaiDianShu().compareTo(dianShu) > 0) {
								sidaierSolutionList.add(i, solution);
								break;
							}
							if (i == length - 1) {
								noWangSandaierSolutionList.add(solution);
							}
							i++;
						}
					}
				}
				// 三带二
				if (dianshuZu instanceof SandaierDianShuZu) {
					SandaierDianShuZu sandaierDianShuZu = (SandaierDianShuZu) dianshuZu;
					DianShu dianshu = sandaierDianShuZu.getSanzhangDianShuArray()[0];
					// DianShu[] daipai = sandaierDianShuZu.getDaipaiDianShuArray();
					if (dianshuCountArray[dianshu.ordinal()] == 3) { // 取只有三张牌的牌组
						if (noWangSandaierSolutionList.isEmpty()) {
							noWangSandaierSolutionList.add(solution);
						} else {
							int length = noWangSandaierSolutionList.size();
							int i = 0;
							while (i < length) {
								SandaierDianShuZu tempDianshuzu = (SandaierDianShuZu) noWangSandaierSolutionList.get(i)
										.getDianShuZu();
								if (tempDianshuzu.getSanzhangDianShuArray()[0]
										.compareTo(sandaierDianShuZu.getSanzhangDianShuArray()[0]) > 0) {
									noWangSandaierSolutionList.add(i, solution);
									break;
								}

								if (i == length - 1) {
									noWangSandaierSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}
				// 对子
				if (dianshuZu instanceof DuiziDianShuZu) {
					DuiziDianShuZu duiziDianShuZu = (DuiziDianShuZu) dianshuZu;
					DianShu dianshu = duiziDianShuZu.getDianShu();
					if (dianshuCountArray[dianshu.ordinal()] < 4) {
						if (dianshuCountArray[dianshu.ordinal()] > 2) {
							if (noWangDuiziYiShangSolutionList.isEmpty()) {
								noWangDuiziYiShangSolutionList.add(solution);
							} else {
								int length1 = noWangDuiziYiShangSolutionList.size();
								int j = 0;
								while (j < length1) {
									if (((DuiziDianShuZu) noWangDuiziYiShangSolutionList.get(j).getDianShuZu())
											.getDianShu().compareTo(duiziDianShuZu.getDianShu()) > 0) {
										noWangDuiziYiShangSolutionList.add(j, solution);
										break;
									}
									if (j == length1 - 1) {
										noWangDuiziYiShangSolutionList.add(solution);
									}
									j++;
								}
							}
						} else {
							if (noWangDuiziSolutionList.isEmpty()) {
								noWangDuiziSolutionList.add(solution);
							} else {
								int length = noWangDuiziSolutionList.size();
								int i = 0;
								while (i < length) {
									if (((DuiziDianShuZu) noWangDuiziSolutionList.get(i).getDianShuZu()).getDianShu()
											.compareTo(duiziDianShuZu.getDianShu()) > 0) {
										noWangDuiziSolutionList.add(i, solution);
										break;
									}
									if (i == length - 1) {
										noWangDuiziSolutionList.add(solution);
									}
									i++;
								}
							}
						}
					}
				}
				// 单张
				if (dianshuZu instanceof DanzhangDianShuZu) {
					DanzhangDianShuZu danzhangDianShuZu = (DanzhangDianShuZu) dianshuZu;
					DianShu dianshu = danzhangDianShuZu.getDianShu();
					if (dianshuCountArray[dianshu.ordinal()] < 4) {
						if (dianshuCountArray[dianshu.ordinal()] > 2) {
							if (noWangDanzhangDuiziYiShangSolutionList.isEmpty()) {
								noWangDanzhangDuiziYiShangSolutionList.add(solution);
							} else {
								int length1 = noWangDanzhangDuiziYiShangSolutionList.size();
								int j = 0;
								while (j < length1) {
									if (((DanzhangDianShuZu) noWangDanzhangDuiziYiShangSolutionList.get(j)
											.getDianShuZu()).getDianShu()
													.compareTo(danzhangDianShuZu.getDianShu()) > 0) {

										noWangDanzhangDuiziYiShangSolutionList.add(j, solution);
										break;
									}
									if (j == length1 - 1) {
										noWangDanzhangDuiziYiShangSolutionList.add(solution);
									}
									j++;
								}
							}
						} else if (dianshuCountArray[dianshu.ordinal()] > 1) {
							if (noWangDanzhangYiShangSolutionList.isEmpty()) {
								noWangDanzhangYiShangSolutionList.add(solution);
							} else {
								int length1 = noWangDanzhangYiShangSolutionList.size();
								int j = 0;
								while (j < length1) {
									if (((DanzhangDianShuZu) noWangDanzhangYiShangSolutionList.get(j).getDianShuZu())
											.getDianShu().compareTo(danzhangDianShuZu.getDianShu()) > 0) {
										noWangDanzhangYiShangSolutionList.add(j, solution);
										break;
									}
									if (j == length1 - 1) {
										noWangDanzhangYiShangSolutionList.add(solution);
									}
									j++;
								}
							}
						} else {
							if (noWangDanzhangSolutionList.isEmpty()) {
								noWangDanzhangSolutionList.add(solution);
							} else {
								int length = noWangDanzhangSolutionList.size();
								int i = 0;
								while (i < length) {
									if (((DanzhangDianShuZu) noWangDanzhangSolutionList.get(i).getDianShuZu())
											.getDianShu().compareTo(danzhangDianShuZu.getDianShu()) > 0) {
										noWangDanzhangSolutionList.add(i, solution);
										break;
									}
									if (i == length - 1) {
										noWangDanzhangSolutionList.add(solution);
									}
									i++;
								}
							}
						}
					}
				}
				// 飞机
				if (dianshuZu instanceof FeijiDianShuZu) {
					FeijiDianShuZu feijiDianShuZu = (FeijiDianShuZu) dianshuZu;
					DianShu[] sanzhangArray = feijiDianShuZu.getSanzhangDianShuArray();
					DianShu[] daipaiArray = feijiDianShuZu.getDaipaiDianShuArray();

					// 不拆炸弹
					boolean boomFlag = false;
					for (int i = 0; i < sanzhangArray.length; i++) {
						int dianshuIndex = sanzhangArray[i].ordinal();
						if (dianshuCountArray[dianshuIndex] != 3) {
							boomFlag = true;
						}
					}
					if (!boomFlag) {
						if (noWangFeijiSolutionList.isEmpty()) {
							noWangFeijiSolutionList.add(solution);
						} else {
							int length = noWangFeijiSolutionList.size();
							int i = 0;
							while (i < length) {
								FeijiDianShuZu tempFeiji = (FeijiDianShuZu) noWangFeijiSolutionList.get(i)
										.getDianShuZu();
								// 不同类型飞机直接加入，同类型比大小
								if (sanzhangArray.length != tempFeiji.getDaipaiDianShuArray().length) {
									noWangFeijiSolutionList.add(solution);
									break;
								} else {
									if (tempFeiji.getSanzhangDianShuArray()[0].compareTo(sanzhangArray[0]) > 0) {
										noWangFeijiSolutionList.add(i, solution);
										break;
									}
									if (i == length - 1) {
										noWangFeijiSolutionList.add(solution);
									}
								}
								i++;
							}
						}
					}
				}
				// 连对
				if (dianshuZu instanceof LianduiDianShuZu) {
					LianduiDianShuZu lianduiDianShuZu = (LianduiDianShuZu) dianshuZu;
					DianShu[] lianXuDianShuArray = lianduiDianShuZu.getLianXuDianShuArray();
					boolean allSuccess = true;
					// for (DianShu dianshu : lianXuDianShuArray) {
					// if (dianshuCountArray[dianshu.ordinal()] != 2) {
					// allSuccess = false;
					// break;
					// }
					// }
					if (allSuccess) {
						if (noWangLianduiSolutionList.isEmpty()) {
							noWangLianduiSolutionList.add(solution);
						} else {
							int length = noWangLianduiSolutionList.size();
							int i = 0;
							while (i < length) {
								if (((LianduiDianShuZu) noWangLianduiSolutionList.get(i).getDianShuZu())
										.getLianXuDianShuArray()[0].compareTo(
												((LianduiDianShuZu) dianshuZu).getLianXuDianShuArray()[0]) > 0) {
									noWangLianduiSolutionList.add(i, solution);
									break;
								}
								if (i == length - 1) {
									noWangLianduiSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}
				// 顺子
				if (dianshuZu instanceof ShunziDianShuZu) {
					ShunziDianShuZu shunziDianShuZu = (ShunziDianShuZu) dianshuZu;
					DianShu[] lianXuDianShuArray = shunziDianShuZu.getLianXuDianShuArray();
					boolean allSuccess = true;
					// for (DianShu dianshu : lianXuDianShuArray) {
					// if (dianshuCountArray[dianshu.ordinal()] != 1) {
					// allSuccess = false;
					// break;
					// }
					// }
					if (allSuccess) {
						if (noWangShunziSolutionList.isEmpty()) {
							noWangShunziSolutionList.add(solution);
						} else {
							int length = noWangShunziSolutionList.size();
							int i = 0;
							while (i < length) {
								if (((ShunziDianShuZu) noWangShunziSolutionList.get(i).getDianShuZu())
										.getLianXuDianShuArray()[0].compareTo(
												((ShunziDianShuZu) dianshuZu).getLianXuDianShuArray()[0]) > 0) {
									noWangShunziSolutionList.add(i, solution);
									break;
								}
								if (i == length - 1) {
									noWangShunziSolutionList.add(solution);
								}
								i++;
							}
						}
					}
				}
			}
		}

		DaPaiDianShuSolution maxZhadanSolution = null;
		for (DaPaiDianShuSolution solution : YaPaiSolutions) {
			DianShuZu dianshuZu = solution.getDianShuZu();
			if (dianshuZu instanceof ZhadanDianShuZu) {
				ZhadanDianShuZu zhadanDianShuZu1 = (ZhadanDianShuZu) dianshuZu;
				if (maxZhadanSolution == null || zhadanComparator.compare(zhadanDianShuZu1,
						(ZhadanDianShuZu) maxZhadanSolution.getDianShuZu()) > 0) {
					maxZhadanSolution = solution;
				}
			}
		}
		if (maxZhadanSolution != null) {
			if (!danGeZhadanSolutionList.isEmpty()) {
				DaPaiDianShuSolution solution = danGeZhadanSolutionList.getLast();
				if (!solution.getDianshuZuheIdx().equals(maxZhadanSolution.getDianshuZuheIdx())) {
					danGeZhadanSolutionList.add(maxZhadanSolution);
				}
			} else {
				danGeZhadanSolutionList.add(maxZhadanSolution);
			}
		}
		noWangDanzhangSolutionList.addAll(noWangDanzhangYiShangSolutionList);
		noWangDanzhangSolutionList.addAll(noWangDanzhangDuiziYiShangSolutionList);
		noWangDuiziSolutionList.addAll(noWangDuiziYiShangSolutionList);

		filtedSolutionList.addAll(noWangDanzhangSolutionList);
		filtedSolutionList.addAll(noWangDuiziSolutionList);
		filtedSolutionList.addAll(noWangSandaierSolutionList);
		filtedSolutionList.addAll(noWangShunziSolutionList);
		filtedSolutionList.addAll(noWangLianduiSolutionList);
		filtedSolutionList.addAll(noWangFeijiSolutionList);

		filtedSolutionList.addAll(danGeZhadanSolutionList);
		return filtedSolutionList;
	}
}
