package com.anbang.qipai.paodekuai.cqrs.c.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dml.paodekuai.pai.dianshuzu.FeijiDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.SandaierDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.comparator.DaipaiComparator;
import com.dml.paodekuai.wanfa.OptionalPlay;
import com.dml.puke.pai.DianShu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DanzhangDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DuiziDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.LianXuDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.LianduiDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.ShunziDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.CanNotCompareException;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.DanGeDianShuZuComparator;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.LianXuDianShuZuComparator;
import com.dml.paodekuai.pai.dianshuzu.DianShuZuCalculator;
import com.dml.paodekuai.pai.dianshuzu.PaiXing;
import com.dml.paodekuai.player.action.da.solution.DaPaiDianShuSolution;
import com.dml.paodekuai.player.action.da.solution.DianShuZuYaPaiSolutionCalculator;

public class PaodekuaiDianShuZuYaPaiSolutionCalculator implements DianShuZuYaPaiSolutionCalculator {
	private OptionalPlay optionalPlay;
	private DanGeDianShuZuComparator danGeDianShuZuComparator;
	private LianXuDianShuZuComparator lianXuDianShuZuComparator;
	private DaipaiComparator daipaiComparator;

	@Override
	public Map<String, DaPaiDianShuSolution> calculate(DianShuZu beiYaDianShuZu, int[] dianShuAmountArray) {
		int[] dianShuAmount = dianShuAmountArray.clone();
		Map<String, DaPaiDianShuSolution> yaPaiSolutionCandidates = new HashMap<>();
		Set<DaPaiDianShuSolution> solutionSet = new HashSet<>();
		// 单张
		if (beiYaDianShuZu instanceof DanzhangDianShuZu) {
			DanzhangDianShuZu beiYaDanzhangDianShuZu = (DanzhangDianShuZu) beiYaDianShuZu;
			// 大小王做单张牌打出必定是作为本身的牌的点数
			List<DanzhangDianShuZu> danzhangDianShuZuList = DianShuZuCalculator
					.calculateDanzhangDianShuZu(dianShuAmount);
			for (DanzhangDianShuZu danzhangDianShuZu : danzhangDianShuZuList) {
				try {
					if (danGeDianShuZuComparator.compare(danzhangDianShuZu, beiYaDanzhangDianShuZu) > 0) {

						DaPaiDianShuSolution solution = new DaPaiDianShuSolution();
						solution.setDianShuZu(danzhangDianShuZu);
						DianShu[] dachuDianShuArray = { danzhangDianShuZu.getDianShu() };
						solution.setDachuDianShuArray(dachuDianShuArray);
						solution.calculateDianshuZuheIdx();
						solutionSet.add(solution);
					}
				} catch (CanNotCompareException e) {

				}
			}
			solutionSet.forEach((solution) -> yaPaiSolutionCandidates.put(solution.getDianshuZuheIdx(), solution));
			return yaPaiSolutionCandidates;
		}

		if (beiYaDianShuZu instanceof DuiziDianShuZu) {
			DuiziDianShuZu beiYaDuiziDianShuZu = (DuiziDianShuZu) beiYaDianShuZu;

		}

		// 跑得快没有王牌
		calculateDaPaiDianShuSolutionWithoutWangDang(dianShuAmount, beiYaDianShuZu, solutionSet);

		// TODO: 2019/3/5 待优化
		solutionSet.forEach((solution) -> {
			DaPaiDianShuSolution daPaiDianShuSolution = yaPaiSolutionCandidates.get(solution.getDianshuZuheIdx());
			if (daPaiDianShuSolution != null) {
				DianShuZu dianShuZu = daPaiDianShuSolution.getDianShuZu();
				// 有可能出现打出点数相同类型却不同的情况
				if (!solution.getDianShuZu().getClass().equals(dianShuZu.getClass())) {

				} else if (dianShuZu instanceof LianXuDianShuZu) {
					try {
						if (lianXuDianShuZuComparator.compare((LianXuDianShuZu) solution.getDianShuZu(),
								(LianXuDianShuZu) dianShuZu) > 0) {
							yaPaiSolutionCandidates.put(solution.getDianshuZuheIdx(), solution);
						}
					} catch (CanNotCompareException e) {
					}
				}
			} else {
				yaPaiSolutionCandidates.put(solution.getDianshuZuheIdx(), solution);
			}
		});
		return yaPaiSolutionCandidates;
	}

	private void calculateDaPaiDianShuSolutionWithoutWangDang(int[] dianshuCountArray, DianShuZu beiYaDianShuZu,
			Set<DaPaiDianShuSolution> solutionSet) {
		//计算剩余手牌数
		int shoupaiCount = 0;
		for (int i = 0; i< 13; i ++) {
			shoupaiCount = shoupaiCount + dianshuCountArray[i];
		}

		PaiXing paiXing = new PaiXing();
		if (beiYaDianShuZu instanceof DuiziDianShuZu) {
			// 对子
			paiXing.setDuiziDianShuZuList(DianShuZuCalculator.calculateDuiziDianShuZu(dianshuCountArray));
		}
		if (beiYaDianShuZu instanceof ShunziDianShuZu) {
			// 顺子
			paiXing.setShunziDianShuZuList(DianShuZuCalculator.calculateShunziDianShuZu(dianshuCountArray));
		}
		if (beiYaDianShuZu instanceof LianduiDianShuZu) {
			// 连对
			paiXing.setLianduiDianShuZuList(DianShuZuCalculator.calculateLianduiDianShuZu(dianshuCountArray));
		}
		if (beiYaDianShuZu instanceof SandaierDianShuZu) {
			// 三带二
			paiXing.setSandaierDianShuZuArrayList(DianShuZuCalculator.calculateSanzhangDianShuZu(dianshuCountArray,
					shoupaiCount, optionalPlay.isSandaique()));
		}
		if (beiYaDianShuZu instanceof FeijiDianShuZu) {
			// 飞机
			paiXing.setFeijiDianShuZuArrayList(DianShuZuCalculator.calculateLiansanzhangDianShuZu(dianshuCountArray,
					shoupaiCount, optionalPlay.isFeijique()));
		}
		paiXing = paiXingFilter(paiXing, beiYaDianShuZu);
		solutionSet.addAll(DianShuZuCalculator.calculateAllDaPaiDianShuSolutionWithoutWangDang(paiXing));
	}

	//牌型比较
	private PaiXing paiXingFilter(PaiXing paiXing, DianShuZu beiYaDianShuZu) {
		PaiXing filtedPaiXing = new PaiXing();
		if (beiYaDianShuZu instanceof DuiziDianShuZu) {
			DuiziDianShuZu beiYaDuiziDianShuZu = (DuiziDianShuZu) beiYaDianShuZu;
			List<DuiziDianShuZu> filtedDuiziDianShuZuList = filtedPaiXing.getDuiziDianShuZuList();
			List<DuiziDianShuZu> duiziDianShuZuList = paiXing.getDuiziDianShuZuList();
			for (DuiziDianShuZu duiziDianShuZu : duiziDianShuZuList) {
				try {
					if (danGeDianShuZuComparator.compare(duiziDianShuZu, beiYaDuiziDianShuZu) > 0) {
						filtedDuiziDianShuZuList.add(duiziDianShuZu);
					}
				} catch (CanNotCompareException e) {

				}
			}
			return filtedPaiXing;
		}
		if (beiYaDianShuZu instanceof ShunziDianShuZu) {
			ShunziDianShuZu beiYaShunziDianShuZu = (ShunziDianShuZu) beiYaDianShuZu;
			List<ShunziDianShuZu> filtedShunziDianShuZu = filtedPaiXing.getShunziDianShuZuList();
			List<ShunziDianShuZu> shunziDianShuZuList = paiXing.getShunziDianShuZuList();
			for (ShunziDianShuZu shunziDianShuZu : shunziDianShuZuList) {
				try {
					if (lianXuDianShuZuComparator.compare(shunziDianShuZu, beiYaShunziDianShuZu) > 0) {
						filtedShunziDianShuZu.add(shunziDianShuZu);
					}
				} catch (CanNotCompareException e) {

				}
			}
			return filtedPaiXing;
		}
		if (beiYaDianShuZu instanceof LianduiDianShuZu) {
			LianduiDianShuZu beiYaLianduiDianShuZu = (LianduiDianShuZu) beiYaDianShuZu;
			List<LianduiDianShuZu> filtedLianduiDianShuZu = filtedPaiXing.getLianduiDianShuZuList();
			List<LianduiDianShuZu> lianduiDianShuZuList = paiXing.getLianduiDianShuZuList();
			for (LianduiDianShuZu lianduiDianShuZu : lianduiDianShuZuList) {
				try {
					if (lianXuDianShuZuComparator.compare(lianduiDianShuZu, beiYaLianduiDianShuZu) > 0) {
						filtedLianduiDianShuZu.add(lianduiDianShuZu);
					}
				} catch (CanNotCompareException e) {

				}
			}
			return filtedPaiXing;
		}
		if (beiYaDianShuZu instanceof SandaierDianShuZu) {
			SandaierDianShuZu beiYaSanzhangDianShuZu = (SandaierDianShuZu) beiYaDianShuZu;
			List<SandaierDianShuZu> filtedSanzhangDianShuZu = filtedPaiXing.getSandaierDianShuZuArrayList();
			List<SandaierDianShuZu> sandaierDianShuZuList = paiXing.getSandaierDianShuZuArrayList();
			for (SandaierDianShuZu sandaierDianShuZu : sandaierDianShuZuList) {
				try {
					if (daipaiComparator.compare(sandaierDianShuZu, beiYaSanzhangDianShuZu) > 0) {
						filtedSanzhangDianShuZu.add(sandaierDianShuZu);
					}
				} catch (CanNotCompareException e) {

				}
			}
			return filtedPaiXing;
		}
		if (beiYaDianShuZu instanceof FeijiDianShuZu) {
			FeijiDianShuZu beiYaLiansanzhangDianShuZu = (FeijiDianShuZu) beiYaDianShuZu;
			List<FeijiDianShuZu> filtedLiansanzhangDianShuZu = filtedPaiXing.getFeijiDianShuZuArrayList();
			List<FeijiDianShuZu> liansanzhangDianShuZuList = paiXing.getFeijiDianShuZuArrayList();
			for (FeijiDianShuZu feijiDianShuZu : liansanzhangDianShuZuList) {
				try {
					if (daipaiComparator.compare(feijiDianShuZu, beiYaLiansanzhangDianShuZu) > 0) {
						filtedLiansanzhangDianShuZu.add(feijiDianShuZu);
					}
				} catch (CanNotCompareException e) {

				}
			}
			return filtedPaiXing;
		}
		return filtedPaiXing;
	}

	public OptionalPlay getOptionalPlay() {
		return optionalPlay;
	}

	public void setOptionalPlay(OptionalPlay optionalPlay) {
		this.optionalPlay = optionalPlay;
	}

	public DanGeDianShuZuComparator getDanGeDianShuZuComparator() {
		return danGeDianShuZuComparator;
	}

	public void setDanGeDianShuZuComparator(DanGeDianShuZuComparator danGeDianShuZuComparator) {
		this.danGeDianShuZuComparator = danGeDianShuZuComparator;
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