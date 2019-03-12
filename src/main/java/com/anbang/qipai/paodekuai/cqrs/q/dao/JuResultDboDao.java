package com.anbang.qipai.paodekuai.cqrs.q.dao;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.JuResultDbo;

public interface JuResultDboDao {

	void save(JuResultDbo juResultDbo);

	JuResultDbo findByGameId(String gameId);

}
