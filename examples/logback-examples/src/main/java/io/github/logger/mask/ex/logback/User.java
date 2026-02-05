package io.github.logger.mask.ex.logback;

import io.github.dsr.mask.core.annotation.Mask;
import io.github.dsr.mask.core.constants.MaskConstants;

import java.util.List;

public class User {

    @Mask(strategy = CustomMaskConstants.NAME)
    private String name;

    @Mask(strategy = MaskConstants.PHONE)
    private String phone;

    @Mask(strategy = CustomMaskConstants.SECRET)
    private String secret;

    @Mask(strategy = MaskConstants.EMAIL)
    private String email;

    private List<User> list;

    @Mask(strategy = MaskConstants.ADDRESS)
    private String address;

    @Mask(strategy = MaskConstants.ID_CAR)
    private String idCar;


    public User(String name, String phone, String secret, String email, String address, String idCar) {
        this.name = name;
        this.phone = phone;
        this.secret = secret;
        this.email = email;
        this.address = address;
        this.idCar = idCar;
    }



    public void setList(List<User> list) {
        this.list = list;
    }


    @Override
    public String toString() {
        return "User@" + System.identityHashCode(this);
    }
}
