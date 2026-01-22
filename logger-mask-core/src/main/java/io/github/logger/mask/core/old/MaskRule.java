package io.github.logger.mask.core.old;

/**
 * @author haiji
 */
public interface MaskRule {

    boolean support(MaskContext context);

    String apply(String message);
}
