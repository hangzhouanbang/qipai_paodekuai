package com.anbang.qipai.paodekuai.cqrs.c.domain;

import com.dml.paodekuai.pai.dianshuzu.ABoomDianShuZu;
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

		//单个炸弹时
		DanGeZhadanDianShuZu danGeZhadan1 = (DanGeZhadanDianShuZu) zhadan1;
		DanGeZhadanDianShuZu danGeZhadan2 = (DanGeZhadanDianShuZu) zhadan2;
		return danGeZhadan1.getDianShu().compareTo(danGeZhadan2.getDianShu());
	}
}
