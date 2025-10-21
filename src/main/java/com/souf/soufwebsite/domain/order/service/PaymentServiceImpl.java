package com.souf.soufwebsite.domain.order.service;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.exception.NotMatchRoleException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.order.dto.OrderReqDto;
import com.souf.soufwebsite.domain.order.dto.OrderResDto;
import com.souf.soufwebsite.domain.order.dto.VerifyReqDto;
import com.souf.soufwebsite.domain.order.dto.portOne.PaymentResDto;
import com.souf.soufwebsite.domain.order.entity.Order;
import com.souf.soufwebsite.domain.order.entity.OrderStatus;
import com.souf.soufwebsite.domain.order.entity.Payment;
import com.souf.soufwebsite.domain.order.exception.MismatchAmountException;
import com.souf.soufwebsite.domain.order.exception.NotFoundOrderException;
import com.souf.soufwebsite.domain.order.exception.NotPaidException;
import com.souf.soufwebsite.domain.order.exception.PortoneNoResponseException;
import com.souf.soufwebsite.domain.order.repository.OrderRepository;
import com.souf.soufwebsite.domain.order.repository.PaymentRepository;
import com.souf.soufwebsite.domain.order.util.PortOneClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    private final PortOneClient oneClient;

    @Override
    @Transactional
    public OrderResDto createOrder(String email, OrderReqDto reqDto) {

        findIfMemberExists(reqDto.clientId(), RoleType.MEMBER);
        findIfMemberExists(reqDto.freelancerId(), RoleType.STUDENT);

        String orderId = "ORD-" + UUID.randomUUID();

        Order order = Order.of(orderId, reqDto);
        orderRepository.save(order);

        return OrderResDto.from(order);
    }

    @Override
    public void verify(String email, VerifyReqDto reqDto) {
        Order order = findIfOrderExists(reqDto.orderId());

        PaymentResDto paymentResDto = oneClient.getPayment(reqDto.paymentId()).block();
        if(paymentResDto == null) {
            throw new PortoneNoResponseException();
        }
        if(!paymentResDto.amount().paid().equals(order.getTotalAmount())){
            throw new MismatchAmountException();
        }
        if(!"PAID".equalsIgnoreCase(paymentResDto.status())){
            throw new NotPaidException();
        }

        Payment payment = Payment.of(order.getOrderUuid(), paymentResDto);
        paymentRepository.save(payment);
        order.updateOrderStatus(OrderStatus.IN_PROGRESS);
    }

    private Member findIfMemberExists(Long memberId, RoleType roleType) {
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
        if(!member.getRole().equals(roleType))
            throw new NotMatchRoleException();

        return member;
    }

    private Order findIfOrderExists(String orderId){
        return orderRepository.findByOrderUuid(orderId).orElseThrow(NotFoundOrderException::new);
    }
}
