package io.github.logger.mask.core.old;

import java.util.List;

public class DefaultMaskEngine implements MaskEngine {

    private final List<MaskRule> rules;

    public DefaultMaskEngine(List<MaskRule> rules) {
        this.rules = rules;
    }

    @Override
    public String mask(String message, MaskContext context) {

        if (message == null || message.isEmpty()) {
            return message;
        }

        // 1️⃣ Marker：NO_MASK
        if (context.hasMarker(MaskConstants.MARKER_NO_MASK)) {
            return message;
        }

        // 2️⃣ MDC：mask=false
        String maskFlag = context.getMdcValue(MaskConstants.MDC_MASK);
        if ("false".equalsIgnoreCase(maskFlag)) {
            return message;
        }

        String result = message;

        for (MaskRule rule : rules) {

            // FORCE_MASK 可跳过 rule.support
            if (context.hasMarker(MaskConstants.MARKER_FORCE_MASK)
                    || rule.support(context)) {
                result = rule.apply(result);
            }
        }

        return result;
    }
}
