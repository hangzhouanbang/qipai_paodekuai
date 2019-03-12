package com.anbang.qipai.paodekuai.msg.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface WatchRecordSource {
    @Output
    MessageChannel watchRecordSink();
}
