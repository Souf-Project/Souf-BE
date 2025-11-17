package com.souf.soufwebsite.domain.recruit.contract.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.lambda.LambdaPdfBody;
import com.souf.soufwebsite.domain.recruit.contract.dto.res.lambda.LambdaWrapperRes;
import com.souf.soufwebsite.domain.recruit.contract.entity.Contract;
import com.souf.soufwebsite.domain.recruit.contract.entity.Project;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractPdfService {

    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;
    private final FileService fileService;

    @Value("${cloud.aws.lambda.name}")
    private String contractFunctionName;

    @Transactional
    public String generateContractPdf(Contract contract) {

        try {
            Map<String, Object> root = new LinkedHashMap<>();
            Map<String, Object> data = new LinkedHashMap<>();
            Map<String, Object> inline = new LinkedHashMap<>();

            root.put("data", data);
            data.put("inline", inline);

            // 1. 계약 당사자
            Map<String, Object> parties = new LinkedHashMap<>();
            Map<String, Object> orderer = new LinkedHashMap<>();
            Map<String, Object> beneficiary = new LinkedHashMap<>();

            orderer.put("회사명", contract.getCompanyName());
            orderer.put("대표자", contract.getOrdererName());
            orderer.put("사업자등록번호", contract.getBusinessRegistrationNumber());
            orderer.put("주소", contract.getRoadAddress());
            orderer.put("전화번호", contract.getContactPhone());
            orderer.put("이메일", contract.getContactEmail());
            orderer.put("담당자 성명/직책", contract.getManagerWithPosition());

            beneficiary.put("이름", contract.getBeneficiaryName());
            beneficiary.put("생년월일", String.valueOf(contract.getBeneficiaryBirth()));
            beneficiary.put("소속 학교", contract.getBeneficiarySchoolInfo());
            beneficiary.put("이메일", contract.getBeneficiaryEmail());
            beneficiary.put("전화번호", contract.getBeneficiaryPhone());
            beneficiary.put("은행 명 및 계좌번호", contract.getBeneficiaryBankAccount());

            parties.put("발주자", orderer);
            parties.put("수급자", beneficiary);
            inline.put("1.계약 당사자", parties);

            Project project = contract.getProject();

            // 3.계약 목적 및 작업 범위
            Map<String, Object> scope = new LinkedHashMap<>();
            scope.put("수행 작업명", project.getProjectName());
            inline.put("3.계약 목적 및 작업 범위", scope);

            // 4.기간 및 일정
            Map<String, Object> period = new LinkedHashMap<>();
            period.put("시작일", String.valueOf(project.getProjectStartDate()));
            period.put("종료일", String.valueOf(project.getProjectEndDate()));
            period.put("일수", project.getProjectProgressingDays());
            inline.put("4. 기간 및 일정", period);

            // 5. 대금 및 지급
            Map<String, Object> payment = new LinkedHashMap<>();
            payment.put("총 계약 금액", project.getTotalContractAmount());
            payment.put("선금 지급 비율", project.getDownPaymentPercentage());
            payment.put("선금 금액", project.getDownPaymentAmount());
            inline.put("5. 대금 및 지급(회원 간 직접)", payment);

            // 6. 검수·수정·자동승인
            Map<String, Object> review = new LinkedHashMap<>();
            review.put("검수기한", project.getInspectionDays());
            inline.put("6. 검수·수정·자동승인", review);

            // 9. 저작권·지식재산권
            Map<String, Object> ip = new LinkedHashMap<>();
            String isApproved = contract.getCopyrightApproved() == Boolean.TRUE ? "허용(비식별화 조건)" : "불허";
            ip.put("허용/불허", isApproved);
            inline.put("9. 저작권·지식재산권", ip);

            // 10. 비밀유지
            Map<String, Object> nda = new LinkedHashMap<>();
            nda.put("유지기간", contract.getConfidentialityPeriod());
            inline.put("10. 비밀유지", nda);

            // 11. 보증·유지보수
            Map<String, Object> warranty = new LinkedHashMap<>();
            warranty.put("보증기간", contract.getWarrantyPeriod());
            inline.put("11. 보증·유지보수", warranty);

            // 13. 분쟁 해결·증빙·관할
            Map<String, Object> dispute = new LinkedHashMap<>();
            dispute.put("관할법원", contract.getCompetentCourt());
            inline.put("13. 분쟁 해결·증빙·관할", dispute);

            Map<String, Object> sign = new LinkedHashMap<>();
            LocalDate now = LocalDate.now();
            sign.put("서명 년", now.getYear());
            sign.put("서명 월", now.getMonthValue());
            sign.put("서명 일", now.getDayOfMonth());
            sign.put("발주자 성명", contract.getOrdererName());
            sign.put("수급자 성명", contract.getBeneficiaryName());
            inline.put("성명", sign);

            // =================== lambda 호출 ==========================

            String requestJson = objectMapper.writeValueAsString(root);

            InvokeResponse generateContractPdf = lambdaClient.invoke(builder -> builder
                    .functionName(contractFunctionName)
                    .invocationType(InvocationType.REQUEST_RESPONSE)
                    .payload(SdkBytes.fromUtf8String(requestJson))
            );

            String lambdaRaw = generateContractPdf.payload().asUtf8String();

            // lambda 응답값 처리

            LambdaWrapperRes lambdaWrapperRes = objectMapper.readValue(lambdaRaw, LambdaWrapperRes.class);
            if(lambdaWrapperRes.statusCode() != 200){
                throw new IllegalStateException("Lambda statusCode != 200 : " + lambdaWrapperRes.statusCode()); // 특정 상태코드가 없음.
            }

            LambdaPdfBody body =
                    objectMapper.readValue(lambdaWrapperRes.body(), LambdaPdfBody.class);

            // Media에 저장 및 반환
            Media contractMetadata = fileService.uploadSingleMedia(body.url(), PostType.CONTRACT, contract.getId());
            contract.completeCreatingContract();

            return contractMetadata.getOriginalUrl();

        } catch (Exception e){
            log.error("계약서 생성 중 발생 : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
