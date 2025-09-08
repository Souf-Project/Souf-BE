package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_VERIFIED_EMAIL;

public class NotVerifiedEmailException extends BaseErrorException {
  public NotVerifiedEmailException() { super(NOT_VERIFIED_EMAIL.getCode(), NOT_VERIFIED_EMAIL.getMessage(), NOT_VERIFIED_EMAIL.getErrorKey()); }
}
