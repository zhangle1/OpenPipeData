package org.pipeData.core.log;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessLogAppender extends UnsynchronizedAppenderBase<LoggingEvent> {

    @Override
    protected void append(LoggingEvent log) {
        System.out.println("------------------>" + log);
    }

}
