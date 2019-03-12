package com.anbang.qipai.paodekuai.zzztest;

import com.alibaba.fastjson.JSON;
import com.dml.paodekuai.ju.Ju;
import com.dml.paodekuai.player.PaodekuaiPlayer;
import com.dml.paodekuai.preparedapai.avaliablepai.CutTypeAvaliablePaiFiller;
import com.dml.puke.pai.PukePai;
import com.dml.puke.pai.PukePaiMian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @Description:
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class InitTest {

    private static Set<PukePaiMian> notPlaySet;

    static {
        notPlaySet = new HashSet<>();
        notPlaySet.add(PukePaiMian.dawang);
        notPlaySet.add(PukePaiMian.xiaowang);
        notPlaySet.add(PukePaiMian.hongxiner);
        notPlaySet.add(PukePaiMian.meihuaer);
        notPlaySet.add(PukePaiMian.fangkuaier);
        notPlaySet.add(PukePaiMian.heitaoA);
    }



     @Test
     public void fillAvaliablePai0()  {
        List<PukePaiMian> playPaiTypeList = new ArrayList<>();
        // 移除不可用牌
        for (PukePaiMian pukePaiMian : PukePaiMian.values()) {
            if (!notPlaySet.contains(pukePaiMian)) {
                playPaiTypeList.add(pukePaiMian);
            }
        }

        List<PukePai> allPaiList = new ArrayList<>();
        // 生成牌
        int id = 0;
        for (PukePaiMian paiType : playPaiTypeList) {
            PukePai pai = new PukePai();
            pai.setId(id);
            pai.setPaiMian(paiType);
            allPaiList.add(pai);
            id++;
        }
    }
}
