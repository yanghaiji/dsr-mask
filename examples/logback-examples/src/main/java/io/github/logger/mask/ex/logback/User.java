package io.github.logger.mask.ex.logback;

import io.github.logger.mask.core.annotation.Mask;

public class User {

    private String name;

    @Mask(type = "phone")
    private String phone;

    @Mask(type = "phone")
    private String secret;

    public User(String name, String phone, String secret) {
        this.name = name;
        this.phone = phone;
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}
