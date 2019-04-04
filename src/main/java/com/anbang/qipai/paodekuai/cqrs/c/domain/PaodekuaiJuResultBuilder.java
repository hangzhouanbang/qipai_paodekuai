package com.anbang.qipai.paodekuai.cqrs.c.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiJuPlayerResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiJuResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiPanPlayerResult;
import com.anbang.qipai.paodekuai.cqrs.c.domain.result.PaodekuaiPanResult;
import com.dml.paodekuai.ju.Ju;
import com.dml.paodekuai.ju.JuResult;
import com.dml.paodekuai.ju.JuResultBuilder;
import com.dml.paodekuai.pan.PanResult;

public class PaodekuaiJuResultBuilder implements JuResultBuilder {

	@Override
	public JuResult buildJuResult(Ju ju) {
		PaodekuaiJuResult paodekuaiJuResult = new PaodekuaiJuResult();
		paodekuaiJuResult.setFinishedPanCount(ju.countFinishedPan());
		if (ju.countFinishedPan() > 0) {
			Map<String, PaodekuaiJuPlayerResult> juPlayerResultMap = new HashMap<>();
			for (PanResult panResult : ju.getFinishedPanResultList()) {
				PaodekuaiPanResult paodekuaiPanResult = (PaodekuaiPanResult) panResult;
				for (PaodekuaiPanPlayerResult panPlayerResult : paodekuaiPanResult.getPanPlayerResultList()) {
					PaodekuaiJuPlayerResult juPlayerResult = juPlayerResultMap.get(panPlayerResult.getPlayerId());
					if (juPlayerResult == null) {
						juPlayerResult = new PaodekuaiJuPlayerResult();
						juPlayerResult.setPlayerId(panPlayerResult.getPlayerId());
						juPlayerResultMap.put(panPlayerResult.getPlayerId(), juPlayerResult);
					}

					if (panPlayerResult.getGuanmenCount() == 1) {
						juPlayerResult.increaseDanguanCount();
					}
					if (panPlayerResult.getGuanmenCount() == 2) {
						juPlayerResult.increaseShuangguanCount();
					}
					if (panPlayerResult.getScore() > juPlayerResult.getMaxScore()) {
						juPlayerResult.setMaxScore(panPlayerResult.getScore());
					}

					juPlayerResult.increaseTotalScore(panPlayerResult.getScore());
					juPlayerResult.increaseBoomCount(panPlayerResult.getZhadanCount());
				}
			}

			PaodekuaiJuPlayerResult dayingjia = null;
			PaodekuaiJuPlayerResult datuhao = null;
			for (PaodekuaiJuPlayerResult juPlayerResult : juPlayerResultMap.values()) {
				if (dayingjia == null) {
					dayingjia = juPlayerResult;
				} else {
					if (juPlayerResult.getTotalScore() > dayingjia.getTotalScore()) {
						dayingjia = juPlayerResult;
					}
				}

				if (datuhao == null) {
					datuhao = juPlayerResult;
				} else {
					if (juPlayerResult.getTotalScore() < datuhao.getTotalScore()) {
						datuhao = juPlayerResult;
					}
				}
			}
			paodekuaiJuResult.setDatuhaoId(datuhao.getPlayerId());
			paodekuaiJuResult.setDayingjiaId(dayingjia.getPlayerId());
			paodekuaiJuResult.setPlayerResultList(new ArrayList<>(juPlayerResultMap.values()));
		}
		return paodekuaiJuResult;
	}

}
