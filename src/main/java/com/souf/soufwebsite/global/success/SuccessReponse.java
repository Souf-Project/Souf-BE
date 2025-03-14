package com.souf.soufwebsite.global.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@ToString
@JsonPropertyOrder({"time", "status", "code", "message", "result"})
public class SuccessReponse<T> {

    @JsonProperty("status")
    private int status;

    private LocalDateTime time;
    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public SuccessReponse(T result) {
        this.status = HttpStatus.OK.value();
        this.time = LocalDateTime.now();
        this.code = SuccessReponseStatus.SUCCESS.getCode();
        this.message = SuccessReponseStatus.SUCCESS.getMessage();
        this.result = result;
    }

    public SuccessReponse(T result, String msg){
        this.status = HttpStatus.OK.value();
        this.time = LocalDateTime.now();
        this.code = SuccessReponseStatus.SUCCESS.getCode();
        this.message = msg;
        this.result = result;
    }

    public static SuccessReponse ok(){
        return SuccessReponse.builder()
                .status(HttpStatus.OK.value())
                .time(LocalDateTime.now())
                .code(SuccessReponseStatus.SUCCESS.getCode())
                .message("SUCCESS")
                .build();
    }

    public static SuccessReponse ok(String msg){
        return SuccessReponse.builder()
                .status(HttpStatus.OK.value())
                .time(LocalDateTime.now())
                .code(SuccessReponseStatus.SUCCESS.getCode())
                .message(msg)
                .build();
    }
}
