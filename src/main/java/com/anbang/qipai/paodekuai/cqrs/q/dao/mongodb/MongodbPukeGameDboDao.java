package com.anbang.qipai.paodekuai.cqrs.q.dao.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.paodekuai.cqrs.q.dao.PukeGameDboDao;
import com.anbang.qipai.paodekuai.cqrs.q.dao.mongodb.repository.PukeGameDboRepository;
import com.anbang.qipai.paodekuai.cqrs.q.dbo.PukeGameDbo;
import com.dml.mpgame.game.player.GamePlayerOnlineState;

@Component
public class MongodbPukeGameDboDao implements PukeGameDboDao {

	@Autowired
	private PukeGameDboRepository repository;

	@Override
	public PukeGameDbo findById(String id) {
		return repository.findOne(id);
	}

	@Override
	public void save(PukeGameDbo pukeGameDbo) {
		repository.save(pukeGameDbo);
	}

	@Override
	public void updatePlayerOnlineState(String id, String playerId, GamePlayerOnlineState onlineState) {
		PukeGameDbo pukeGameDbo = repository.findOne(id);
		pukeGameDbo.getPlayers().forEach((player) -> {
			if (player.getPlayerId().equals(playerId)) {
				player.setOnlineState(onlineState);
			}
		});
		repository.save(pukeGameDbo);
	}

}
