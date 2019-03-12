package com.anbang.qipai.paodekuai.plan.dao;

import com.anbang.qipai.paodekuai.plan.bean.MemberGoldBalance;

public interface MemberGoldBalanceDao {

	void save(MemberGoldBalance memberGoldBalance);

	MemberGoldBalance findByMemberId(String memberId);
}
