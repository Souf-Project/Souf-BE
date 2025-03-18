package com.souf.soufwebsite.domain.user.dto.ReqDto;


import jakarta.validation.constraints.NotNull;

public record SigninReqDto(
        @NotNull String email,
        @NotNull String password
){

}
