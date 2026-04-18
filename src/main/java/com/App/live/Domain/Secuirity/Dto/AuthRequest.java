package com.App.live.Domain.Secuirity.Dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}