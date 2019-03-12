package com.anbang.qipai.paodekuai.cqrs.q.dao;

import com.anbang.qipai.paodekuai.cqrs.q.dbo.GameLatestPanActionFrameDbo;
import com.dml.paodekuai.pan.PanActionFrame;

public interface GameLatestPanActionFrameDboDao {

	GameLatestPanActionFrameDbo findById(String id);

	void save(String id, PanActionFrame panActionFrame);

}
