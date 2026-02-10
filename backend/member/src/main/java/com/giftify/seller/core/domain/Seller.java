package com.giftify.seller.core.domain;

import com.giftify.common.domain.IdentifiableDomain;
import com.giftify.seller.core.exception.SellerDomainException;
import com.giftify.seller.core.exception.SellerErrorCode;

import java.time.LocalDateTime;

public class Seller  extends IdentifiableDomain<Long> {

    Long memberId;                 // FK → Member

    // 판매자 기본 정보
    SellerType type;               // INDIVIDUAL(크리에이터), BUSINESS(브랜드)
    String sellerName;             // 스토어명 / 판매자명
    SellerStatus status;           // PENDING(승인대기), ACTIVE(승인완료), SUSPENDED(정지), REJECTED(반려)

    // 사업자 정보 (nullable: 개인사업자인 경우)
    String businessNumber;         // 사업자등록번호
    String businessName;           // 상호명
    String representativeName;     // 대표자명
    String businessAddress;        // 사업장 주소

    // 정산 정보 (법적·회계적 책임의 종착지)
    BankCode bankCode;
    String accountNumber; // 정산받을 계좌번호
    String accountHolder; // 예금주/사업자명/개인명

    // 정책 동의
    boolean settlementAgreement;   // 정산 약관 동의 여부
    LocalDateTime agreementAt;

    // 운영 메타 정보
    LocalDateTime approvedAt;      // 관리자 승인 시점
    LocalDateTime createdAt;       // 판매자 신청 시점
    LocalDateTime updatedAt;

    private Seller(
            Long memberId,
            SellerType type,
            String sellerName,

            // 정산 정보 (필수)
            BankCode bankCode,
            String accountNumber,
            String accountHolder,

            // 사업자 정보 (nullable)
            String businessNumber,
            String businessName,
            String representativeName,
            String businessAddress
    ) {
        super();

        validate(memberId != null, new SellerDomainException(SellerErrorCode.INVALID_MEMBER_ID));
        validate(type != null, new SellerDomainException(SellerErrorCode.INVALID_SELLER_TYPE));
        validate(sellerName != null && !sellerName.isBlank(), new SellerDomainException(SellerErrorCode.INVALID_SELLER_NAME));
        validate(bankCode != null &&
                        accountNumber != null && !accountNumber.isBlank() &&
                        accountHolder != null && !accountHolder.isBlank(),
                new SellerDomainException(SellerErrorCode.INVALID_SETTLEMENT_INFO));

        // 개인 사업자가 아닐 경우 사업자 정보 필수
        if (type.isBusiness()) {
            validate(
                    businessNumber != null &&
                            businessName != null &&
                            representativeName != null &&
                            businessAddress != null,
                    new SellerDomainException(SellerErrorCode.INVALID_BUSINESS_INFO)
            );
        }

        this.memberId = memberId;
        this.type = type;
        this.sellerName = sellerName;
        this.status = SellerStatus.PENDING;

        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;

        this.settlementAgreement = true;
        this.agreementAt = LocalDateTime.now();

        this.businessNumber = businessNumber;
        this.businessName = businessName;
        this.representativeName = representativeName;
        this.businessAddress = businessAddress;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // DB 복원용
    private Seller(
            Long id,
            Long memberId,
            SellerType type,
            String sellerName,
            SellerStatus status,
            String businessNumber,
            String businessName,
            String representativeName,
            String businessAddress,
            BankCode bankCode,
            String accountNumber,
            String accountHolder,
            boolean settlementAgreement,
            LocalDateTime agreementAt,
            LocalDateTime approvedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        super(id);
        this.memberId = memberId;
        this.type = type;
        this.sellerName = sellerName;
        this.status = status;
        this.businessNumber = businessNumber;
        this.businessName = businessName;
        this.representativeName = representativeName;
        this.businessAddress = businessAddress;
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.settlementAgreement = settlementAgreement;
        this.agreementAt = agreementAt;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // [프론트엔드] 판매자가 정산 정책에 동의하지 않는다면, 판매자 등록이 불가능하게 강제
    public static Seller create(
            Long memberId,
            SellerType type,
            String sellerName,

            BankCode bankCode,
            String accountNumber,
            String accountHolder,

            String businessNumber,
            String businessName,
            String representativeName,
            String businessAddress
    ) {
        return new Seller(
                memberId,
                type,
                sellerName,

                bankCode,
                accountNumber,
                accountHolder,

                businessNumber,
                businessName,
                representativeName,
                businessAddress
        );
    }

    // DB 복원용
    public static Seller reconstruct(
            Long id,
            Long memberId,
            SellerType type,
            String sellerName,
            SellerStatus status,
            String businessNumber,
            String businessName,
            String representativeName,
            String businessAddress,
            BankCode bankCode,
            String accountNumber,
            String accountHolder,
            boolean settlementAgreement,
            LocalDateTime agreementAt,
            LocalDateTime approvedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Seller(
                id,
                memberId,
                type,
                sellerName,
                status,
                businessNumber,
                businessName,
                representativeName,
                businessAddress,
                bankCode,
                accountNumber,
                accountHolder,
                settlementAgreement,
                agreementAt,
                approvedAt,
                createdAt,
                updatedAt
        );
    }

    // 상태 전이
    public void approve() {
        validate(status.canApprove(),
                new SellerDomainException(SellerErrorCode.INVALID_STATUS_TRANSITION));

        validateBeforeApprove();

        this.status = SellerStatus.ACTIVE;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = this.approvedAt;
    }

    public void reject() {
        validate(status.canReject(),
                new SellerDomainException(SellerErrorCode.INVALID_STATUS_TRANSITION));

        this.status = SellerStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        validate(status.canSuspend(),
                new SellerDomainException(SellerErrorCode.INVALID_STATUS_TRANSITION));

        this.status = SellerStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void releaseSuspension() {
        validate(status.canReleaseSuspension(),
                new SellerDomainException(SellerErrorCode.INVALID_STATUS_TRANSITION));

        this.status = SellerStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void reapply() {
        validate(status.canReapply(),
                new SellerDomainException(SellerErrorCode.INVALID_STATUS_TRANSITION));

        this.status = SellerStatus.PENDING;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeSettlementInfo(
            BankCode bankCode,
            String accountNumber,
            String accountHolder
    ) {
        validate(status != SellerStatus.REJECTED,
                new SellerDomainException(SellerErrorCode.INVALID_STATUS_TRANSITION));

        validate(bankCode != null &&
                        accountNumber != null &&
                        !accountNumber.isBlank() &&
                        accountHolder != null &&
                        !accountHolder.isBlank(),
                new SellerDomainException(SellerErrorCode.INVALID_SETTLEMENT_INFO));

        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.updatedAt = LocalDateTime.now();
    }

    public void registerBusinessInfo(
            String businessNumber,
            String businessName,
            String representativeName,
            String businessAddress
    ) {
        validate(type.isBusiness(),
                new SellerDomainException(SellerErrorCode.BUSINESS_INFO_NOT_ALLOWED));

        validate(status == SellerStatus.PENDING,
                new SellerDomainException(SellerErrorCode.INVALID_STATUS_TRANSITION));

        validate(
                businessNumber != null &&
                        businessName != null &&
                        representativeName != null &&
                        businessAddress != null,
                new SellerDomainException(SellerErrorCode.INVALID_BUSINESS_INFO)
        );

        this.businessNumber = businessNumber;
        this.businessName = businessName;
        this.representativeName = representativeName;
        this.businessAddress = businessAddress;
        this.updatedAt = LocalDateTime.now();
    }

    // 승인 전 다시 한 번 상태 검증
    private void validateBeforeApprove() {
        validate(settlementAgreement,
                new SellerDomainException(SellerErrorCode.SETTLEMENT_AGREEMENT_REQUIRED));

        if (type.isBusiness()) {
            validate(
                    businessNumber != null &&
                            businessName != null &&
                            representativeName != null &&
                            businessAddress != null,
                    new SellerDomainException(SellerErrorCode.BUSINESS_INFO_REQUIRED)
            );
        }
    }

    // Getters
    public Long getMemberId() {
        return memberId;
    }

    public SellerType getType() {
        return type;
    }

    public String getSellerName() {
        return sellerName;
    }

    public SellerStatus getStatus() {
        return status;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public BankCode getBankCode() {
        return bankCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public boolean isSettlementAgreement() {
        return settlementAgreement;
    }

    public LocalDateTime getAgreementAt() {
        return agreementAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
