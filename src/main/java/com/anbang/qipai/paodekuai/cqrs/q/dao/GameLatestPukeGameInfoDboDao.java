package com.anbang.qipai.paodekuai.cqrs.q.dao;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.GameLatestPukeGameInfoDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameInfoDbo;

public interface GameLatestPukeGameInfoDboDao {
	GameLatestPukeGameInfoDbo findById(String id);

	void save(String id, PukeGameInfoDbo gameInfoDbo);
}
