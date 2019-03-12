package com.anbang.qipai.paodekuai.cqrs.c.domain.listener;

import java.util.HashMap;
import java.util.Map;

import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.ZhadanDianShuZu;
import com.dml.paodekuai.ju.Ju;
import com.dml.paodekuai.player.action.da.DaAction;
import com.dml.paodekuai.player.action.da.DaActionStatisticsListener;

/**
 * 记录每盘每个玩家打出的炸弹数
 */
public class BoomCountDaActionStatisticsListener implements DaActionStatisticsListener {

    private Map<String, Integer> playerzhadanshuMap = new HashMap<>();

    @Override
    public void updateForNextPan() {
        playerzhadanshuMap = new HashMap<>();
    }

    @Override
    public void update(DaAction daAction, Ju ju) {
        String daActionPlayerId = daAction.getActionPlayerId();
        Integer boomCount = playerzhadanshuMap.get(daActionPlayerId);
        if (boomCount == null) {
            boomCount = 0;
        }
        DianShuZu dianShuZu = daAction.getDachuPaiZu().getDianShuZu();
        if (dianShuZu instanceof ZhadanDianShuZu) {
            boomCount++;
        }
    }

    public Map<String, Integer> getPlayerzhadanshuMap() {
        return playerzhadanshuMap;
    }

    public void setPlayerzhadanshuMap(Map<String, Integer> playerzhadanshuMap) {
        this.playerzhadanshuMap = playerzhadanshuMap;
    }
}
