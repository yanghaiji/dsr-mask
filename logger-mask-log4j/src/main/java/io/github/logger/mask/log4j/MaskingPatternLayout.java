package io.github.logger.mask.log4j;

import io.github.logger.mask.core.old.DefaultMaskEngine;
import io.github.logger.mask.core.old.MaskContext;
import io.github.logger.mask.core.old.MaskEngine;
import io.github.logger.mask.core.old.PhoneMaskRule;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Plugin(
        name = "MaskingPatternLayout",
        category = Node.CATEGORY,
        elementType = Layout.ELEMENT_TYPE
)
public class MaskingPatternLayout extends AbstractStringLayout {

    private static final MaskEngine MASK_ENGINE =
            new DefaultMaskEngine(Arrays.asList(
                    new PhoneMaskRule()
            ));

    private final PatternLayout delegate;

    protected MaskingPatternLayout(PatternLayout delegate) {
        super(Charset.defaultCharset());
        this.delegate = delegate;
    }

    @PluginFactory
    public static MaskingPatternLayout createLayout(
            @PluginAttribute("pattern") String pattern) {

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern(pattern)
                .build();

        return new MaskingPatternLayout(layout);
    }

    @Override
    public String toSerializable(LogEvent event) {

        String msg = delegate.toSerializable(event);

        Set<String> markers = new HashSet<>();
        if (event.getMarker() != null) {
            markers.add(event.getMarker().getName());
        }

        MaskContext context = new MaskContext(
                event.getLoggerName(),
                event.getLevel().name(),
                event.getContextData().toMap(),
                markers
        );

        return MASK_ENGINE.mask(msg, context);
    }
}

