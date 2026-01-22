package io.github.logger.mask.core;

public class PhoneMaskStrategy implements MaskStrategy {

    @Override
    public String type() {
        return "phone";
    }

    @Override
    public String mask(String origin, String[] args) {
        if (origin == null || origin.length() < 7) {
            return origin;
        }

        return origin.substring(0, 3) + "****" + origin.substring(origin.length() - 4);
    }
}
