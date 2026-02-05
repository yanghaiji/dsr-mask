package io.github.dsr.response.example;

import io.github.dsr.mask.core.annotation.Mask;
import io.github.dsr.mask.core.constants.MaskConstants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class User {

    private String name;

    @Mask(strategy = MaskConstants.PHONE)
    private String phone;

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



    @Override
    public String toString() {
        return "User@" + System.identityHashCode(this);
    }
}
