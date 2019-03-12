package com.anbang.qipai.paodekuai.cqrs.q.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.paodekuai.cqrs.q.dao.GameLatestPukeGameInfoDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.mongodb.repository.GameLatestPukeGameInfoDboRepository;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.GameLatestPukeGameInfoDbo;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameInfoDbo;

@Component
public class MongodbGameLatestPukeGameInfoDboDao implements GameLatestPukeGameInfoDboDao {

	@Autowired
	private GameLatestPukeGameInfoDboRepository repository;

	@Override
	public GameLatestPukeGameInfoDbo findById(String id) {
		return repository.findOne(id);
	}

	@Override
	public void save(String id, PukeGameInfoDbo gameInfoDbo) {
		GameLatestPukeGameInfoDbo dbo = new GameLatestPukeGameInfoDbo();
		dbo.setId(id);
		dbo.setPukeGameInfoDbo(gameInfoDbo);
		repository.save(dbo);
	}

}
