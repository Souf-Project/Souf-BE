package com.souf.soufwebsite.domain.order.service;

import com.souf.soufwebsite.domain.order.dto.OrderReqDto;
import com.souf.soufwebsite.domain.order.dto.OrderResDto;
import com.souf.soufwebsite.domain.order.dto.VerifyReqDto;

public interface PaymentService {

    OrderResDto createOrder(String email, OrderReqDto reqDto);

    void verify(String email, VerifyReqDto reqDto);
}
