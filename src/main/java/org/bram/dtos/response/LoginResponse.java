package org.bram.dtos.response;

import lombok.Data;

@Data
public class LoginResponse {

    private String userId;
    private String message;
    private boolean success;
}
