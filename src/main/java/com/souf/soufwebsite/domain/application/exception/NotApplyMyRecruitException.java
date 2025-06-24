package com.souf.soufwebsite.domain.application.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.application.exception.ErrorType.NOT_APPLY_MY_RECRUIT;

public class NotApplyMyRecruitException extends BaseErrorException {
    public NotApplyMyRecruitException() {
        super(NOT_APPLY_MY_RECRUIT.getCode(), NOT_APPLY_MY_RECRUIT.getMessage());
    }
}
