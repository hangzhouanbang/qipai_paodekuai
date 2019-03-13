package com.anbang.qipai.paodekuai.msg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

import com.anbang.qipai.paodekuai.msg.channel.PaodekuaiResultSource;
import com.anbang.qipai.paodekuai.msg.msjobj.CommonMO;
import com.anbang.qipai.paodekuai.msg.msjobj.PukeHistoricalJuResult;
import com.anbang.qipai.paodekuai.msg.msjobj.PukeHistoricalPanResult;

@EnableBinding(PaodekuaiResultSource.class)
public class PaodekuaiResultMsgService {

	@Autowired
	private PaodekuaiResultSource paodekuaiResultSource;

	public void recordJuResult(PukeHistoricalJuResult juResult) {
		CommonMO mo = new CommonMO();
		mo.setMsg("paodekuai ju result");
		mo.setData(juResult);
		paodekuaiResultSource.paodekuaiResult().send(MessageBuilder.withPayload(mo).build());
	}

	public void recordPanResult(PukeHistoricalPanResult panResult) {
		CommonMO mo = new CommonMO();
		mo.setMsg("paodekuai pan result");
		mo.setData(panResult);
		paodekuaiResultSource.paodekuaiResult().send(MessageBuilder.withPayload(mo).build());
	}
}
