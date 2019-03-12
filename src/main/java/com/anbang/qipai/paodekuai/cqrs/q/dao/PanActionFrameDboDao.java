package com.anbang.qipai.paodekuai.cqrs.q.dao;

import java.util.List;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.PanActionFrameDbo;

public interface PanActionFrameDboDao {

	void save(PanActionFrameDbo dbo);

	List<PanActionFrameDbo> findByGameIdAndPanNo(String gameId, int panNo);

	PanActionFrameDbo findByGameIdAndPanNo(String gameId, int panNo, int actionNo);
}
