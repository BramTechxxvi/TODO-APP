package org.bram.dtos.requests;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}
