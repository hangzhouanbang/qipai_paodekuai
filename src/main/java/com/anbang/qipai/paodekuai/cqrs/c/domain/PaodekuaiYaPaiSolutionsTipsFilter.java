package com.anbang.qipai.paodekuai.cqrs.c.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dml.paodekuai.pai.dianshuzu.DaiPaiZhaDanDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.FeijiDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.SandaierDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.SidaierDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.SidaisanDianShuZu;
import com.dml.paodekuai.player.action.da.YaPaiSolutionsTipsFilter;
import com.dml.paodekuai.player.action.da.solution.DaPaiDianShuSolution;
import com.dml.paodekuai.wanfa.OptionalPlay;
import com.dml.puke.pai.DianShu;
import com.dml.puke.pai.PukePai;
import com.dml.puke.wanfa.dianshu.dianshuzu.DanGeZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DanzhangDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DuiziDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.LianduiDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.SanzhangDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.ShunziDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.ZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.ZhadanComparator;

/**
 * 提示顺序： 没有王牌代替的打法、不拆炸弹、同种类型
 */
public class PaodekuaiYaPaiSolutionsTipsFilter implements YaPaiSolutionsTipsFilter {

	private ZhadanComparator zhadanComparator;

	private OptionalPlay optionalPlay;

	@Override
	public List<DaPaiDianShuSolution> filter(List<DaPaiDianShuSolution> YaPaiSolutions,
			Map<Integer, PukePai> allShoupai, boolean yapai) {
		List<DaPaiDianShuSolution> filtedSolutionList = new LinkedList<>();
		List<DaPaiDianShuSolution> noLaiziSolutionList = new ArrayList<>(YaPaiSolutions);

		// 手牌转为点数数量数组
		int[] dianshuCountArray = new int[15];
		for (PukePai pukePai : allShoupai.values()) {
			DianShu dianShu = pukePai.getPaiMian().dianShu();
			dianshuCountArray[dianShu.ordinal()]++;
		}

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
							} else if (compare == 0) {
								DianShu danpai = danGeZhadanDianShuZu1.getDaipaiDian();
								DianShu dp = null;
								if (danGeZhadanDianShuZu2 instanceof DaiPaiZhaDanDianShuZu) {
									DaiPaiZhaDanDianShuZu danGeZhadanDianShuZu3 = (DaiPaiZhaDanDianShuZu) danGeZhadanDianShuZu2;
									dp = danGeZhadanDianShuZu3.getDaipaiDian();
								}
								if (dp != null) {
									if (dp.compareTo(danpai) > 0) {
										danGeZhadanSolutionList.add(i, solution);
										break;
									}
								}
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
								DianShu ds = danGeZhadanDianShuZu2.getDianShu();
								if (danGeZhadanDianShuZu2 instanceof DaiPaiZhaDanDianShuZu) {
									DaiPaiZhaDanDianShuZu danGeZhadanDianShuZu3 = (DaiPaiZhaDanDianShuZu) danGeZhadanDianShuZu2;
									ds = danGeZhadanDianShuZu3.getZhadanDian();
								}
								int compare = ds.compareTo(dianshu);
								if (compare >= 0) {
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
								sidaierSolutionList.add(solution);
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

					if (sidaisanSolutionList.isEmpty()) {
						sidaisanSolutionList.add(solution);
					} else {
						int length = sidaisanSolutionList.size();
						int i = 0;
						while (i < length) {
							SidaisanDianShuZu tempDianshuzu = (SidaisanDianShuZu) sidaisanSolutionList.get(i)
									.getDianShuZu();
							if (tempDianshuzu.getDanpaiDianShu().compareTo(dianShu) > 0) {
								sidaisanSolutionList.add(i, solution);
								break;
							}
							if (i == length - 1) {
								sidaisanSolutionList.add(solution);
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
								sidaierSolutionList.add(solution);
							}
							i++;
						}
					}
				}
				// 四带三
				if (dianshuZu instanceof SidaisanDianShuZu) {
					final SidaisanDianShuZu sidaisanDianShuZu = (SidaisanDianShuZu) dianshuZu;
					DianShu dianShu = sidaisanDianShuZu.getDanpaiDianShu();
					if (sidaisanSolutionList.isEmpty()) {
						sidaisanSolutionList.add(solution);
					} else {
						int length = sidaisanSolutionList.size();
						int i = 0;
						while (i < length) {
							SidaisanDianShuZu tempDianshuzu = (SidaisanDianShuZu) sidaisanSolutionList.get(i)
									.getDianShuZu();
							int compare = tempDianshuzu.getDanpaiDianShu().compareTo(dianShu);
							if (compare > 0) {
								sidaisanSolutionList.add(i, solution);
								break;
							} else if (compare == 0) {
								int ordinal1 = 0;
								DianShu[] daipais1 = sidaisanDianShuZu.getDaipaiDianShuArray();
								for (DianShu d : daipais1) {
									ordinal1 += d.ordinal();
								}
								int ordinal2 = 0;
								DianShu[] daipais2 = tempDianshuzu.getDaipaiDianShuArray();
								for (DianShu d : daipais2) {
									ordinal2 += d.ordinal();
								}
								if (ordinal2 > ordinal1) {
									noWangSandaierSolutionList.add(i, solution);
									break;
								}
							}
							if (i == length - 1) {
								sidaisanSolutionList.add(solution);
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
								int compare = tempDianshuzu.getSanzhangDianShuArray()[0]
										.compareTo(sandaierDianShuZu.getSanzhangDianShuArray()[0]);
								if (compare > 0) {
									noWangSandaierSolutionList.add(i, solution);
									break;
								} else if (compare == 0) {
									int ordinal1 = 0;
									DianShu[] daipais1 = sandaierDianShuZu.getDaipaiDianShuArray();
									for (DianShu d : daipais1) {
										ordinal1 += d.ordinal();
									}
									int ordinal2 = 0;
									DianShu[] daipais2 = tempDianshuzu.getDaipaiDianShuArray();
									for (DianShu d : daipais2) {
										ordinal2 += d.ordinal();
									}
									if (ordinal2 > ordinal1) {
										noWangSandaierSolutionList.add(i, solution);
										break;
									}
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

	public ZhadanComparator getZhadanComparator() {
		return zhadanComparator;
	}

	public void setZhadanComparator(ZhadanComparator zhadanComparator) {
		this.zhadanComparator = zhadanComparator;
	}

	public OptionalPlay getOptionalPlay() {
		return optionalPlay;
	}

	public void setOptionalPlay(OptionalPlay optionalPlay) {
		this.optionalPlay = optionalPlay;
	}
}
