package io.github.logger.mask.core.old;

import java.util.Map;
import java.util.Set;

public class MaskContext {

    private final String loggerName;
    private final String level;
    private final Map<String, String> mdc;
    private final Set<String> markers;

    public MaskContext(
            String loggerName,
            String level,
            Map<String, String> mdc,
            Set<String> markers) {
        this.loggerName = loggerName;
        this.level = level;
        this.mdc = mdc;
        this.markers = markers;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public String getLevel() {
        return level;
    }

    public Map<String, String> getMdc() {
        return mdc;
    }

    public boolean hasMarker(String marker) {
        return markers != null && markers.contains(marker);
    }

    public String getMdcValue(String key) {
        return mdc == null ? null : mdc.get(key);
    }
}
