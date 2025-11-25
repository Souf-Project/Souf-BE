package com.souf.soufwebsite.domain.recruit.contract.service;

import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.exception.NotFoundChatRoomException;
import com.souf.soufwebsite.domain.chat.repository.ChatRoomRepository;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.exception.NotFoundMediaException;
import com.souf.soufwebsite.domain.file.repository.MediaRepository;
import com.souf.soufwebsite.domain.file.service.S3UploaderService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.profile.CompanyProfile;
import com.souf.soufwebsite.domain.member.entity.profile.StudentProfile;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.BeneficiaryReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.req.OrdererReqDto;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.*;
import com.souf.soufwebsite.domain.recruit.contract.entity.Contract;
import com.souf.soufwebsite.domain.recruit.contract.entity.ContractInvite;
import com.souf.soufwebsite.domain.recruit.contract.entity.ContractStatus;
import com.souf.soufwebsite.domain.recruit.contract.entity.Project;
import com.souf.soufwebsite.domain.recruit.contract.exception.*;
import com.souf.soufwebsite.domain.recruit.contract.repository.ContractInviteRepository;
import com.souf.soufwebsite.domain.recruit.contract.repository.ContractRepository;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.util.TokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ContractInviteRepository contractInviteRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MediaRepository mediaRepository;

    private final ContractPdfService contractPdfService;
    private final S3UploaderService s3UploaderService;

    private final TokenUtils tokenUtils;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    @Transactional
    public CreateInitialContractResDto createContractWithOrderer(String email, Long roomId, OrdererReqDto ordererReqDto, HttpServletResponse response) {

        Member orderer = getCurrentMember(email);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(NotFoundChatRoomException::new);
        log.info("chatRoomId: {} and roomId: {}", chatRoom.getId(), roomId);

        for(Contract c : chatRoom.getContracts()){
            if(c.getContractStatus() != ContractStatus.SIGNED)
                throw new AlreadyExistsProgressingContractException();
        }

        if(!orderer.getId().equals(chatRoom.getSender().getId())) {
            throw new NotAcceptedMemberException();
        }

        Member beneficiary = chatRoom.getReceiver();

        Project project = new Project(ordererReqDto);
        Contract contract = new Contract(ordererReqDto, orderer, beneficiary, chatRoom);

        contract.attachProject(project);

        contractRepository.save(contract);

        String opaqueToken = tokenUtils.newOpaqueToken(48);
        Instant expireTime = Instant.now().plus(6, ChronoUnit.HOURS);

        ContractInvite contractInvite = new ContractInvite(opaqueToken, contract.getContractUuid(),
                beneficiary.getId(), roomId, expireTime);
        contractInviteRepository.save(contractInvite);

        sendInviteToken(response, opaqueToken, expireTime.getEpochSecond());

        return new CreateInitialContractResDto(contract.getContractUuid());
    }

    @Override
    @Transactional
    public PresignedUrlResDto uploadFinalContractMedia(String email, Long roomId, MediaReqDto reqDto) {
        Member currentMember = getCurrentMember(email);

        ChatRoom chatRoom = chatRoomRepository.findByIdAndSender(roomId, currentMember).orElseThrow(NotFoundChatRoomException::new);
        Contract contract = contractRepository.findByChatRoom(chatRoom).orElseThrow(NotFoundContractException::new);
        if(contract.getContractStatus() == ContractStatus.SIGNED){
            throw new AlreadySignedContractException();
        }

        List<Media> contractMetadata = mediaRepository.findByPostTypeAndPostId(PostType.CONTRACT, contract.getId());
        Media media;
        if(contractMetadata.isEmpty()){
            throw new NotFoundMediaException();
        }
        media = contractMetadata.get(0);

        String key = media.getOriginalUrl();
        contract.updateFinalContractStatus();

        return s3UploaderService.regeneratePresignedUploadUrl(key, media.getFileName());
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
    public PreviewBeneficiaryInfoRes previewBeneficiaryInfo(String email, HttpServletRequest request, Long currentChatRoomId) {
        Member currentMember = getCurrentMember(email);
        StudentProfile profile = null;

        if(currentMember.getStudentProfile() != null){
            profile = currentMember.getStudentProfile();
        }

        String inviteToken = getInviteTokenFromHeader(request);

        findAndValidateContractInvite(inviteToken, currentChatRoomId, currentMember.getId());

        return PreviewBeneficiaryInfoRes.of(currentMember, profile);
    }

    @Override
    @Transactional
    public InitialContractResDto getIncompleteContractInfo(String email, HttpServletRequest request, Long currentChatRoomId) {
        Member currentBeneficiary = getCurrentMember(email);
        String inviteToken = getInviteTokenFromHeader(request);

        ContractInvite currentContractInvite = findAndValidateContractInvite(inviteToken, currentChatRoomId, currentBeneficiary.getId());

        Contract contract = contractRepository
                .findByContractUuid(currentContractInvite.getContractUuid()).orElseThrow(NotFoundContractException::new);

        log.info("수급자가 계약서 {}를 조회하였습니다.", contract.getContractUuid());
        currentContractInvite.updateViewTiming();


        return InitialContractResDto.of(contract, contract.getProject());
    }


    @Override
    @Transactional
    public CreateContractPdfResDto acceptContractByInvite(String email, Long chatroomId, BeneficiaryReqDto reqDto,
                                                          HttpServletRequest request, HttpServletResponse response) {

        Member currentBeneficiary = getCurrentMember(email);
        String inviteToken = getInviteTokenFromHeader(request);

        ContractInvite ci =
                findAndValidateContractInvite(inviteToken, chatroomId, currentBeneficiary.getId());
        ci.consume();

        Contract currentContract = contractRepository.findByContractUuid(ci.getContractUuid()).orElseThrow(NotFoundContractException::new);

        if(!currentContract.getContractUuid().equals(ci.getContractUuid()))
            throw new NotAcceptedContractException();
        validateContractStatus(currentContract);

        if(!currentContract.getBeneficiary().equals(currentBeneficiary)) {
            throw new NotAcceptedMemberException();
        }


        currentContract.updateBeneficiaryInfo(reqDto);

        return contractPdfService.generateContractPdf(currentContract);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaResDto getSignedContractPdfInChatRoom(String email, Long currentChatRoomId) {
        Member currentMember = getCurrentMember(email);

        ChatRoom chatRoom = chatRoomRepository.findByMember(currentChatRoomId, currentMember).orElseThrow(NotFoundChatRoomException::new);
        Contract contract = contractRepository.findByChatRoom(chatRoom).orElseThrow(NotFoundContractException::new);

        List<Media> contractMetadata = mediaRepository.findByPostTypeAndPostId(PostType.CONTRACT, contract.getId());
        Media media;
        if(contractMetadata.isEmpty()){
            throw new NotFoundMediaException();
        }
        media = contractMetadata.get(0);

        return MediaResDto.fromMedia(media);
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

    private static String getInviteTokenFromHeader(HttpServletRequest request) {
        if (request.getCookies() == null) throw new NotExistsInviteTokenException(); // 예외처리
        for (Cookie cookie : request.getCookies()) {
            if ("Invite-Token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new NotExistsInviteTokenException();
    }

    private void sendInviteToken(HttpServletResponse res, String inviteToken, long inviteTokenExpiration) {
        boolean isProd = "prod".equalsIgnoreCase(activeProfile);

        String sameSite = isProd ? "None" : "Lax";

        ResponseCookie cookie = ResponseCookie.from("InviteToken", inviteToken)
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .maxAge(Duration.ofMillis(inviteTokenExpiration))
                .sameSite(sameSite)
                .build();

        res.addHeader("Set-Cookie", cookie.toString());
    }
}
