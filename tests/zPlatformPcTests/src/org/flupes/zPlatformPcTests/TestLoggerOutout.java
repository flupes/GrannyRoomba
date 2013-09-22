package org.flupes.zPlatformPcTests;

import org.slf4j.LoggerFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

public class TestLoggerOutout {

    protected static org.slf4j.Logger s_logger = LoggerFactory.getLogger("grannyroomba");

    /**
     * @param args
     */
    public static void main(String[] args) {
            Logger logger_impl = Logger.getLogger("grannyroomba");
            logger_impl.setLevel(Level.DEBUG);
            Appender appender = new ConsoleAppender(new TTCCLayout(), ConsoleAppender.SYSTEM_OUT);
            logger_impl.addAppender(appender);

    s_logger.trace("this is a trace message");
    s_logger.debug("this is a debug message");
    s_logger.info("this is an info message");
    s_logger.error("this is an error message");
            
    }

}
