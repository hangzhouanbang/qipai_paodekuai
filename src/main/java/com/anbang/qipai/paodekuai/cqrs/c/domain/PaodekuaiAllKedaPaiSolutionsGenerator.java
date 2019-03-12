package com.anbang.qipai.paodekuai.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dml.paodekuai.pai.dianshuzu.PaodekuaiDianShuZuGenerator;
import com.dml.paodekuai.pai.dianshuzu.comparator.DaipaiComparator;
import com.dml.paodekuai.wanfa.OptionalPlay;
import com.dml.puke.pai.DianShu;
import com.dml.puke.pai.PukePai;
import com.dml.puke.pai.PukePaiMian;
import com.dml.puke.wanfa.dianshu.dianshuzu.DanzhangDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZuGenerator;
import com.dml.puke.wanfa.dianshu.dianshuzu.DuiziDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.LianXuDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.ZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.CanNotCompareException;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.LianXuDianShuZuComparator;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.ZhadanComparator;
import com.dml.paodekuai.pai.dianshuzu.DianShuZuCalculator;
import com.dml.paodekuai.pai.dianshuzu.PaiXing;
import com.dml.paodekuai.pai.jiesuanpai.DawangDangPai;
import com.dml.paodekuai.pai.jiesuanpai.ShoupaiJiesuanPai;
import com.dml.paodekuai.pai.jiesuanpai.XiaowangDangPai;
import com.dml.paodekuai.player.action.da.AllKedaPaiSolutionsGenerator;
import com.dml.paodekuai.player.action.da.solution.DaPaiDianShuSolution;

public class PaodekuaiAllKedaPaiSolutionsGenerator implements AllKedaPaiSolutionsGenerator {
    private OptionalPlay optionalPlay;

    private ZhadanComparator zhadanComparator;

    private LianXuDianShuZuComparator lianXuDianShuZuComparator;

    private DaipaiComparator daipaiComparator;

    @Override
    public Map<String, DaPaiDianShuSolution> generateAllKedaPaiSolutions(Map<Integer, PukePai> allShoupai, boolean baodan) {
        Map<String, DaPaiDianShuSolution> yaPaiSolutionCandidates = new HashMap<>();
        Set<DaPaiDianShuSolution> solutionSet = new HashSet<>();

        //所有手牌的点数数量数组
        int[] dianshuCountArray = new int[15];
        for (PukePai pukePai : allShoupai.values()) {
            DianShu dianShu = pukePai.getPaiMian().dianShu();
            dianshuCountArray[dianShu.ordinal()]++;
        }

        //可打的单张牌
        if (baodan) {
            List<DanzhangDianShuZu> danzhangDianShuZuList = PaodekuaiDianShuZuGenerator.largestDanzhangDianshuzu(dianshuCountArray);
            solutionSet.addAll(DianShuZuCalculator.generateAllDanzhangDaPaiDianShuSolution(danzhangDianShuZuList));
        } else {
            List<DanzhangDianShuZu> danzhangDianShuZuList = DianShuZuGenerator.generateAllDanzhangDianShuZu(dianshuCountArray);
            solutionSet.addAll(DianShuZuCalculator.generateAllDanzhangDaPaiDianShuSolution(danzhangDianShuZuList));
        }

        // 其他牌型的出牌方案
        calculateDaPaiDianShuSolutionWithoutWangDang(dianshuCountArray, solutionSet, allShoupai.size());

        solutionSet.forEach((solution) -> {
            DaPaiDianShuSolution daPaiDianShuSolution = yaPaiSolutionCandidates.get(solution.getDianshuZuheIdx());
            // TODO: 2019/3/12
            //跑的快这里不考虑癞子玩法时会出现点数相同类型不同的情况
            yaPaiSolutionCandidates.put(solution.getDianshuZuheIdx(), solution);
        });
        return yaPaiSolutionCandidates;
    }

    @Override
    public Map<String, DaPaiDianShuSolution> firstAllKedaPaiSolutions(Map<Integer, PukePai> allShoupai) {
        // 不是首张必出黑桃三即为一般方法
        Map<String, DaPaiDianShuSolution> yaPaiSolutionCandidates = generateAllKedaPaiSolutions(allShoupai, false);
        if (!optionalPlay.isBichu()) {
            return yaPaiSolutionCandidates;
        }

        // TODO: 2019/3/12 优化
        for (DaPaiDianShuSolution solution : yaPaiSolutionCandidates.values()) {
            solution.getBichuPai().add(PukePaiMian.heitaosan);
        }
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
        // 三带二
        paiXing.setSandaierDianShuZuArrayList(DianShuZuCalculator.calculateSanzhangDianShuZu(dianshuCountArray,
                shoupaiCount, optionalPlay.isSandaique()));
        // 飞机
        paiXing.setFeijiDianShuZuArrayList(DianShuZuCalculator.calculateLiansanzhangDianShuZu(dianshuCountArray,
                shoupaiCount, optionalPlay.isFeijique()));
        // 普通炸弹
        paiXing.setDanGeZhadanDianShuZuList(DianShuZuCalculator.calculateDanGeZhadanDianShuZu(dianshuCountArray));

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
