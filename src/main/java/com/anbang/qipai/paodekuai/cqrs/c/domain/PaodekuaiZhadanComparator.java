package com.anbang.qipai.paodekuai.cqrs.c.domain;

import com.dml.paodekuai.pai.dianshuzu.ABoomDianShuZu;
import com.dml.paodekuai.pai.dianshuzu.DaiPaiZhaDanDianShuZu;
import com.dml.puke.pai.DianShu;
import com.dml.puke.wanfa.dianshu.dianshuzu.DanGeZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.ZhadanDianShuZu;
import com.dml.puke.wanfa.dianshu.dianshuzu.comparator.ZhadanComparator;

public class PaodekuaiZhadanComparator implements ZhadanComparator {

	@Override
	public int compare(ZhadanDianShuZu zhadan1, ZhadanDianShuZu zhadan2) {
		// 有aaa炸弹时
		if (zhadan1 instanceof ABoomDianShuZu) {
			return 1;
		}
		if (zhadan2 instanceof ABoomDianShuZu) {
			return -1;
		}

		DianShu dianShu1 = getDianShu(zhadan1);
		DianShu dianshu2 = getDianShu(zhadan2);

		return dianShu1.compareTo(dianshu2);
	}

	private static DianShu getDianShu(ZhadanDianShuZu zhadanDianShuZu) {
		DianShu dianShu = null;
		if (zhadanDianShuZu instanceof DaiPaiZhaDanDianShuZu) {
			DaiPaiZhaDanDianShuZu daipaiDian = (DaiPaiZhaDanDianShuZu) zhadanDianShuZu;
			dianShu = daipaiDian.getZhadanDian();
			return dianShu;
		}
		if (zhadanDianShuZu instanceof DanGeZhadanDianShuZu) {
			DanGeZhadanDianShuZu danGeZhadan = (DanGeZhadanDianShuZu) zhadanDianShuZu;
			dianShu = danGeZhadan.getDianShu();
		}
		return dianShu;
	}
}
