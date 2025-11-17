package com.souf.soufwebsite.domain.recruit.contract.service;

import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.exception.NotFoundChatRoomException;
import com.souf.soufwebsite.domain.chat.repository.ChatRoomRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.profile.CompanyProfile;
import com.souf.soufwebsite.domain.member.entity.profile.StudentProfile;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.BeneficiaryReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.JoinByInviteReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.OrdererReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.CreateInitialContractResDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.InitialContractResDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.PreviewBeneficiaryInfoRes;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.PreviewOrdererInfoRes;
import com.souf.soufwebsite.domain.recruit.contract.entity.Contract;
import com.souf.soufwebsite.domain.recruit.contract.entity.ContractInvite;
import com.souf.soufwebsite.domain.recruit.contract.entity.ContractStatus;
import com.souf.soufwebsite.domain.recruit.contract.entity.Project;
import com.souf.soufwebsite.domain.recruit.contract.exception.*;
import com.souf.soufwebsite.domain.recruit.contract.repository.ContractInviteRepository;
import com.souf.soufwebsite.domain.recruit.contract.repository.ContractRepository;
import com.souf.soufwebsite.global.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ContractInviteRepository contractInviteRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final ContractPdfService contractPdfService;

    private final TokenUtils tokenUtils;

    @Override
    @Transactional
    public CreateInitialContractResDto createContractWithOrderer(String email, Long roomId, OrdererReqDto ordererReqDto) {

        Member orderer = getCurrentMember(email);
        if(!orderer.getId().equals(ordererReqDto.ordererId()))
            throw new NotAcceptedMemberException();

        Member beneficiary = findIfMemberExistsById(ordererReqDto.beneficiaryId());

        ChatRoom chatRoom = findIfChatroomExists(orderer, beneficiary);
        log.info("chatRoomId: {} and roomId: {}", chatRoom.getId(), roomId);
        if(!chatRoom.getId().equals(roomId)){
            throw new NotAcceptedChatroomException();
        }

        Project project = new Project(ordererReqDto);
        Contract contract = new Contract(ordererReqDto, roomId, orderer, beneficiary);

        contract.attachProject(project);

        contractRepository.save(contract);

        String opaqueToken = tokenUtils.newOpaqueToken(48);
        Instant expireTime = Instant.now().plus(6, ChronoUnit.HOURS);

        ContractInvite contractInvite = new ContractInvite(opaqueToken, contract.getContractUuid(),
                beneficiary.getId(), roomId, expireTime);
        contractInviteRepository.save(contractInvite);

        return new CreateInitialContractResDto(contract.getContractUuid(), opaqueToken);
    }



    @Override
    public PreviewOrdererInfoRes previewOrdererInfo(String email, Long currentRoomId) {
        Member currentMember = getCurrentMember(email);
        CompanyProfile profile = null;

        chatRoomRepository.findByIdAndSender(currentRoomId, currentMember).orElseThrow(NotFoundChatRoomException::new);

        if(currentMember.getCompanyProfile() != null)
            profile = currentMember.getCompanyProfile();

        return PreviewOrdererInfoRes.from(currentMember, profile);
    }

    @Override
    public PreviewBeneficiaryInfoRes previewBeneficiaryInfo(String email,JoinByInviteReqDto reqDto, Long currentChatRoomId) {
        Member currentMember = getCurrentMember(email);
        StudentProfile profile = null;

        if(currentMember.getStudentProfile() != null){
            profile = currentMember.getStudentProfile();
        }

        findAndValidateContractInvite(reqDto.inviteToken(), currentChatRoomId, currentMember.getId());

        return PreviewBeneficiaryInfoRes.of(currentMember, profile);
    }

    @Override
    @Transactional
    public InitialContractResDto getIncompleteContractInfo(String email, JoinByInviteReqDto reqDto, Long currentChatRoomId) {
        Member currentBeneficiary = getCurrentMember(email);

        ContractInvite currentContractInvite = findAndValidateContractInvite(reqDto.inviteToken(), currentChatRoomId, currentBeneficiary.getId());

        Contract contract = contractRepository
                .findByContractUuid(currentContractInvite.getContractUuid()).orElseThrow(NotFoundContractException::new);

        log.info("수급자가 계약서 {}를 조회하였습니다.", contract.getContractUuid());
        currentContractInvite.updateViewTiming();


        return InitialContractResDto.of(contract, contract.getProject());
    }


    @Override
    @Transactional
    public String acceptContractByInvite(String email, Long contractId, Long chatroomId, BeneficiaryReqDto reqDto) {

        Member currentBeneficiary = getCurrentMember(email);

        ContractInvite ci =
                findAndValidateContractInvite(reqDto.inviteToken(), chatroomId, currentBeneficiary.getId());
        ci.consume();

        Contract currentContract = contractRepository.findById(contractId).orElseThrow(NotFoundContractException::new);

        if(!currentContract.getContractUuid().equals(ci.getContractUuid()))
            throw new NotAcceptedContractException();
        validateContractStatus(currentContract);

        if(!currentContract.getBeneficiary().equals(currentBeneficiary)) {
            throw new NotAcceptedMemberException();
        }


        currentContract.updateBeneficiaryInfo(reqDto);

        return contractPdfService.generateContractPdf(currentContract);
    }

    // private 메서드

    private ChatRoom findIfChatroomExists(Member orderer, Member beneficiary) {
        return chatRoomRepository.findBySenderAndReceiver(orderer, beneficiary).orElseThrow(NotFoundChatRoomException::new);
    }

    private Member getCurrentMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }

    private Member findIfMemberExistsById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
    }

    private void validateContractStatus(Contract contract) {
        if(contract.getContractStatus() == ContractStatus.COMPLETED || contract.getContractStatus() == ContractStatus.CREATING_CONTRACT)
            throw new AlreadySignedContractException();
    }

    private ContractInvite findAndValidateContractInvite(String inviteToken, Long chatRoomId, Long beneficiaryId) {

        ContractInvite ci = contractInviteRepository
                .findById(inviteToken).orElseThrow(NotFoundContractInviteException::new);

        if(ci.getIsConsumed() != null)
            throw new AlreadyConsumedInviteTokenException();
        if(ci.isRevoked())
            throw new RevokedInviteTokenException();
        if(ci.getExpiresAt() != null && Instant.now().isAfter(ci.getExpiresAt()))
            throw new AlreadyExpiredInviteTokenException();
        if(ci.getChatRoomId() != null && !(ci.getChatRoomId().equals(chatRoomId)))
            throw new NotAcceptedChatroomException();
        if(ci.getBeneficiaryId() != null && !ci.getBeneficiaryId().equals(beneficiaryId))
            throw new NotAcceptedMemberException();

        return ci;
    }
}
