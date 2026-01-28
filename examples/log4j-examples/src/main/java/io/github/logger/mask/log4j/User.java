package io.github.logger.mask.log4j;


import io.github.dsr.mask.core.annotation.Mask;
import io.github.dsr.mask.core.constants.MaskConstants;

import java.util.List;

public class User {

    private String name;

    @Mask(type = MaskConstants.PHONE)
    private String phone;

    private String secret;

    @Mask(type = MaskConstants.EMAIL)
    private String email;

    private List<User> list;

    public User(String name, String phone, String secret, String email) {
        this.name = name;
        this.phone = phone;
        this.secret = secret;
        this.email = email;
    }


    public void setList(List<User> list) {
        this.list = list;
    }


    @Override
    public String toString() {
        return "User@" + System.identityHashCode(this);
    }
}
