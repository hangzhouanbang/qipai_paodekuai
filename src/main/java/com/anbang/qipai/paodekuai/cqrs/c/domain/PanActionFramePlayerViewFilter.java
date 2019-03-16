package com.anbang.qipai.paodekuai.cqrs.c.domain;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.GameLatestPanActionFrameDbo;
import com.dml.puke.wanfa.position.Position;
import com.dml.puke.wanfa.position.PositionUtil;
import com.dml.paodekuai.pan.PanActionFrame;
import com.dml.paodekuai.pan.PanValueObject;
import com.dml.paodekuai.player.PaodekuaiPlayerValueObject;

/**
 * 双明的过滤
 * 
 * @author lsc
 *
 */
public class PanActionFramePlayerViewFilter {
	public PanActionFrame filter(GameLatestPanActionFrameDbo frame, String playerId) {

		PanValueObject pan = frame.getPanActionFrame().getPanAfterAction();
		pan.getPaiListValueObject().setPaiList(null);

		// 打完时可看到所有牌，否则只看自己的牌
		for (PaodekuaiPlayerValueObject player : pan.getPaodekuaiPlayerList()) {
			if (player.getTotalShoupai() == 0) {
				return frame.getPanActionFrame();
			}
		}
		for (PaodekuaiPlayerValueObject player : pan.getPaodekuaiPlayerList()) {
			if (player.getId().equals(playerId)) {// 是自己
				// 什么都不过滤，全要看
			} else {// 是其他玩家
				player.setAllShoupai(null);
				player.setLiangPaiList(null);
				player.setShoupaiDianShuAmountArray(null);
				player.setShoupaiIdListForSortList(null);
				player.setYaPaiSolutionCandidates(null);
				player.setYaPaiSolutionsForTips(null);
			}
		}
		return frame.getPanActionFrame();
	}
}
