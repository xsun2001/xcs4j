package io.xsun.xcs4j.base.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

public class Log4jSystemLoggerFinder extends System.LoggerFinder {
    @Override
    public System.Logger getLogger(String name, Module module) {
        return new Log4jLogger(LogManager.getLogger(name));
    }

    public static class Log4jLogger implements System.Logger {
        private final Logger agency;

        public Log4jLogger(Logger agency) {
            this.agency = agency;
        }

        @Override
        public String getName() {
            return agency.getName();
        }

        private org.apache.logging.log4j.Level transformToLog4jLevel(Level level){
            switch (level){
                case ALL:
                    return org.apache.logging.log4j.Level.ALL;
                case TRACE:
                    return org.apache.logging.log4j.Level.TRACE;
                case DEBUG:
                    return org.apache.logging.log4j.Level.DEBUG;
                case INFO:
                    return org.apache.logging.log4j.Level.INFO;
                case WARNING:
                    return org.apache.logging.log4j.Level.WARN;
                case ERROR:
                    return org.apache.logging.log4j.Level.ERROR;
                case OFF:
                    return org.apache.logging.log4j.Level.OFF;
                default:
                    return null;
            }
        }

        @Override
        public boolean isLoggable(Level level) {
            return agency.isEnabled(transformToLog4jLevel(level));
        }

        @Override
        public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
            agency.log(transformToLog4jLevel(level), msg, thrown);
        }

        @Override
        public void log(Level level, ResourceBundle bundle, String format, Object... params) {
            agency.log(transformToLog4jLevel(level), format, params);
        }
    }
}
