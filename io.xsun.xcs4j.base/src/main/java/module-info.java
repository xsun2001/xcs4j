import io.xsun.xcs4j.base.log.Log4jSystemLoggerFinder;

module io.xsun.xcs4j.base {
    requires transitive org.apache.logging.log4j;
    exports io.xsun.xcs4j.base;
    exports io.xsun.xcs4j.base.log;
    provides java.lang.System.LoggerFinder with Log4jSystemLoggerFinder;
}