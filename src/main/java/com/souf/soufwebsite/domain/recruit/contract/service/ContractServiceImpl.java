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
import com.souf.soufwebsite.domain.recruit.contract.entity.ContractStatus;
import com.souf.soufwebsite.domain.recruit.contract.entity.Project;
import com.souf.soufwebsite.domain.recruit.contract.exception.AlreadyExistsProgressingContractException;
import com.souf.soufwebsite.domain.recruit.contract.exception.AlreadySignedContractException;
import com.souf.soufwebsite.domain.recruit.contract.exception.NotAcceptedMemberException;
import com.souf.soufwebsite.domain.recruit.contract.exception.NotFoundContractException;
import com.souf.soufwebsite.domain.recruit.contract.repository.ContractRepository;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MediaRepository mediaRepository;

    private final ContractPdfService contractPdfService;
    private final S3UploaderService s3UploaderService;

    @Override
    @Transactional
    public CreateInitialContractResDto createContractWithOrderer(String email, Long roomId, OrdererReqDto ordererReqDto) {

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

//        String opaqueToken = tokenUtils.newOpaqueToken(48);
//        Instant expireTime = Instant.now().plus(6, ChronoUnit.HOURS);
//
//        ContractInvite contractInvite = new ContractInvite(opaqueToken, contract.getContractUuid(),
//                beneficiary.getId(), roomId, expireTime);
//        contractInviteRepository.save(contractInvite);

        return new CreateInitialContractResDto(contract.getContractUuid());
    }

    @Override
    @Transactional
    public PresignedUrlResDto uploadFinalContractMedia(String email, Long roomId, MediaReqDto reqDto) {
        Member currentMember = getCurrentMember(email);

        ChatRoom chatRoom = chatRoomRepository.findByIdAndSender(roomId, currentMember).orElseThrow(NotFoundChatRoomException::new);
        Contract contract = contractRepository.findByChatRoomAndContractStatus(chatRoom, ContractStatus.COMPLETED).orElseThrow(NotFoundContractException::new);
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
    public PreviewBeneficiaryInfoRes previewBeneficiaryInfo(String email, Long currentChatRoomId) {
        Member currentMember = getCurrentMember(email);
        StudentProfile profile = null;

        if(currentMember.getStudentProfile() != null){
            profile = currentMember.getStudentProfile();
        }

        findIfChatroomExists(currentMember, currentChatRoomId);

        return PreviewBeneficiaryInfoRes.of(currentMember, profile);
    }

    @Override
    @Transactional
    public InitialContractResDto getIncompleteContractInfo(String email, Long currentChatRoomId) {
        Member currentBeneficiary = getCurrentMember(email);

        ChatRoom chatRoom = chatRoomRepository.findByIdAndReceiver(currentChatRoomId, currentBeneficiary).orElseThrow(NotFoundChatRoomException::new);
        Contract contract = contractRepository.findByBeneficiaryAndChatRoomAndContractStatus_PendingCounterpart(currentBeneficiary, chatRoom, ContractStatus.PENDING_COUNTERPART)
                .orElseThrow(NotFoundContractException::new);

        log.info("수급자가 계약서 {}를 조회하였습니다.", contract.getContractUuid());


        return InitialContractResDto.of(contract, contract.getProject());
    }


    @Override
    @Transactional
    public CreateContractPdfResDto acceptContractByInvite(String email, Long chatroomId, BeneficiaryReqDto reqDto) {

        Member currentBeneficiary = getCurrentMember(email);

        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId).orElseThrow(NotFoundChatRoomException::new);
        Contract currentContract = contractRepository.findByBeneficiaryAndChatRoomAndContractStatus_PendingCounterpart(currentBeneficiary, chatRoom, ContractStatus.PENDING_COUNTERPART)
                .orElseThrow(NotFoundContractException::new);

        validateContractStatus(currentContract);

        currentContract.updateBeneficiaryInfo(reqDto);

        return contractPdfService.generateContractPdf(currentContract);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SignedContractResDto> getSignedContractPdfInChatRoom(String email, Long currentChatRoomId) {
        Member currentMember = getCurrentMember(email);

        ChatRoom chatRoom = chatRoomRepository.findByMember(currentChatRoomId, currentMember).orElseThrow(NotFoundChatRoomException::new);
        List<Contract> contracts = contractRepository.findByMember(chatRoom, currentMember);

        return contracts.stream().map(
                contract -> {
                    List<Media> contractMetadata = mediaRepository.findByPostTypeAndPostId(PostType.CONTRACT, contract.getId());
                    Media media;
                    media = contractMetadata.get(0) == null ? null : contractMetadata.get(0);
                    MediaResDto mediaResDto = MediaResDto.fromMedia(Objects.requireNonNull(media));

                    return new SignedContractResDto(contract.getContractUuid(), contract.getProject().getProjectName(), mediaResDto);
                }
        ).toList();
    }

    // private 메서드

    private ChatRoom findIfChatroomExists(Member member, Long chatRoomId) {
        return chatRoomRepository.findByIdAndReceiver(chatRoomId, member).orElseThrow(NotFoundChatRoomException::new);
    }

    private Member getCurrentMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }

    private Member findIfMemberExistsById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
    }

    private void validateContractStatus(Contract contract) {
        if(contract.getContractStatus() == ContractStatus.COMPLETED || contract.getContractStatus() == ContractStatus.CREATING_CONTRACT || contract.getContractStatus() == ContractStatus.SIGNED)
            throw new AlreadySignedContractException();
    }
}
