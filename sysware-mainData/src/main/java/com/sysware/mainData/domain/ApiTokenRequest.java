package com.sysware.mainData.domain;

import lombok.Data;

@Data
public class ApiTokenRequest {

    private String username;
    private String password;

    @Override
    public String toString() {
        return "ApiTokenRequest{" +
                "username='" + username + '\'' +
                ", password='" + (password != null ? password : "") + '\'' +
                '}';
    }
}
