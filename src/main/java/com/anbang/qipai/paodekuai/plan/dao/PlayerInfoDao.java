package com.anbang.qipai.paodekuai.plan.dao;

import com.anbang.qipai.paodekuai.plan.bean.PlayerInfo;

public interface PlayerInfoDao {

	PlayerInfo findById(String playerId);

	void save(PlayerInfo playerInfo);
}
