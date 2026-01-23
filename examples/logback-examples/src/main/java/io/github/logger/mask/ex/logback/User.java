package io.github.logger.mask.ex.logback;

import io.github.logger.mask.core.annotation.Mask;
import io.github.logger.mask.core.constants.MaskConstants;

public class User {

    private String name;

    @Mask(type = MaskConstants.PHONE)
    private String phone;

    @Mask(type = CustomMaskConstants.SECRET)
    private String secret;

    @Mask(type = MaskConstants.EMAIL)
    private String email;

    public User(String name, String phone, String secret, String email) {
        this.name = name;
        this.phone = phone;
        this.secret = secret;
        this.email = email;
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
