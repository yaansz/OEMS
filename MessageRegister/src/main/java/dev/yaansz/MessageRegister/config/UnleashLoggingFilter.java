package dev.yaansz.MessageRegister.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

public class UnleashLoggingFilter extends TurboFilter {

    private UnleashService unleashService;

    public void setUnleashService(UnleashService unleashService) {
        this.unleashService = unleashService;
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (unleashService == null) {
            return FilterReply.NEUTRAL;
        }

        if (level == Level.DEBUG) {
            // Check global debug logging first
            if (unleashService.isDebugLoggingEnabledGlobally()) {
                return FilterReply.ACCEPT;
            }

            // Extract module name from logger name
            String loggerName = logger.getName();
            String moduleName = extractModuleName(loggerName);

            // Check module-specific debug logging
            if (unleashService.isDebugLoggingEnabled(moduleName)) {
                return FilterReply.ACCEPT;
            }

            // Deny debug logs if not enabled
            return FilterReply.DENY;
        }

        return FilterReply.NEUTRAL;
    }

    private String extractModuleName(String loggerName) {
        if (loggerName == null || loggerName.isEmpty()) {
            return "unknown";
        }

        // Extract the simple class name
        int lastDot = loggerName.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < loggerName.length() - 1) {
            return loggerName.substring(lastDot + 1);
        }

        return loggerName;
    }
}
