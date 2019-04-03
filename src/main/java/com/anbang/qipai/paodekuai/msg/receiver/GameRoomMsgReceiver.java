package com.anbang.qipai.paodekuai.msg.receiver;

import java.util.ArrayList;
import java.util.List;

import com.anbang.qipai.paodekuai.cqrs.c.domain.PukeGameValueObject;
import com.anbang.qipai.paodekuai.cqrs.q.service.PukeGameQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.paodekuai.cqrs.c.service.GameCmdService;
import com.anbang.qipai.paodekuai.msg.channel.GameRoomSink;
import com.anbang.qipai.paodekuai.msg.msjobj.CommonMO;
import com.dml.mpgame.game.GameValueObject;
import com.google.gson.Gson;

@EnableBinding(GameRoomSink.class)
public class GameRoomMsgReceiver {

	@Autowired
	private GameCmdService gameCmdService;

	@Autowired
	private PukeGameQueryService pukeGameQueryService;

	private Gson gson = new Gson();

	@StreamListener(GameRoomSink.PAODEKUAIGAMEROOM)
	public void removeGameRoom(CommonMO mo) {
		String msg = mo.getMsg();
		String json = gson.toJson(mo.getData());
		if ("gameIds".equals(msg)) {
			List<String> gameIds = gson.fromJson(json, ArrayList.class);
			for (String gameId : gameIds) {
				PukeGameValueObject gameValueObject;
				try {
					gameValueObject = gameCmdService.finishGameImmediately(gameId);
					pukeGameQueryService.finishGameImmediately(gameValueObject);
				} catch (Exception e) {
				}
			}
		}
	}

}
