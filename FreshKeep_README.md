# FreshKeep — 식품 유통기한 관리 시스템

> 식품 물류 현장에서 발생하는 유통기한 관리, 콜드체인 검증, 재고 격리 문제를 도메인 중심으로 설계한 마이크로서비스

---

## 프로젝트 배경

전 직장(한국식품과학연구원)에서 식품 시험성적서 검토 및 발행 업무를 담당하며, 제조사가 검체를 분석기관에 의뢰하고 **적합 판정을 받아야만 제품이 출하**되는 프로세스를 직접 경험했습니다.

이 경험을 바탕으로, 단순 재고 수량 관리가 아닌 **식품 안전 규칙이 코드 레벨에서 강제되는 시스템**을 설계했습니다.

---

## 핵심 설계 원칙

### 1. 상태 전이를 enum으로 강제
`StockStatus`를 String이 아닌 enum으로 관리하여, 잘못된 상태 전이를 컴파일 시점에 차단합니다.

```
PENDING_INSPECTION → AVAILABLE  (검사 적합)
PENDING_INSPECTION → HOLD       (검사 부적합 / 온도 이탈)
AVAILABLE          → HOLD       (품질 이슈 발생)
AVAILABLE          → WARNING    (유통기한 20% 이하)
HOLD               → AVAILABLE  (격리 해제)
HOLD               → DISPOSED   (폐기 확정)
DISPOSED           → (전이 불가) ← 폐기 후 되돌릴 수 없음
```

> 전 직장에서 한 번 폐기 판정난 검체는 절대 출하될 수 없는 것처럼,
> 이 규칙을 시스템이 보장합니다.

---

### 2. Hold 시 수량 차감 없이 상태 전이로 관리
격리(Hold) 처리 시 재고 수량을 즉시 차감하지 않습니다.

**이유**: 폐기 확정 전까지 재고는 물리적으로 존재하며, 격리 해제 가능성도 있습니다. 수량 차감 대신 상태 전이로 관리하면 이력 추적과 롤백이 용이합니다.

---

### 3. FEFO(First Expired, First Out) 출고 순서 보장
동일 제품의 여러 배치(Lot) 중 **유통기한이 가까운 것부터 자동으로 출고**합니다. Hold 상태인 배치는 자동으로 제외됩니다.

---

### 4. 입고 시 온도 적합성 자동 검증
입고 시 입력된 보관 구역 온도와 제품의 허용 온도 범위를 자동으로 비교합니다. 범위를 벗어나면 해당 배치를 즉시 Hold 처리하고 이력을 기록합니다.

---

## 도메인 모델

### PRODUCT (제품 마스터)
```sql
PRODUCT_NO        VARCHAR(10)   PK
PRODUCT_NAME      VARCHAR(100)
STORAGE_TYPE      VARCHAR(15)   -- FROZEN / REFRIGERATED / AMBIENT
MIN_TEMPERATURE   DECIMAL(4,1)  -- 허용 최저 온도 (ex: -18.0)
MAX_TEMPERATURE   DECIMAL(4,1)  -- 허용 최고 온도 (ex: -15.0)
```

### STOCK (배치별 재고)
```sql
STOCK_ID          BIGINT        PK (auto increment)
PRODUCT_NO        VARCHAR(10)   FK
LOT_NO            VARCHAR(20)   -- 배치번호 (ex: LOT-20250101-001)
MFG_DATE          DATE          -- 제조일자
EXPIRY_DATE       DATE          -- 유통기한
AMOUNT            INT
STOCK_STATUS      VARCHAR(20)   -- PENDING_INSPECTION / AVAILABLE / WARNING / HOLD / EXPIRED / DISPOSED
```

> 한 줄 = 한 배치(Lot)의 현재 상태
> 같은 제품이라도 입고 시점이 다르면 별도 행으로 관리됩니다.

### STOCK_MOVEMENT (재고 이동 이력)
```sql
MOVEMENT_ID       BIGINT        PK
STOCK_ID          BIGINT        FK
PRODUCT_NO        VARCHAR(10)
MOVEMENT_TYPE     VARCHAR(20)   -- INBOUND / OUTBOUND / HOLD / RELEASE / DISPOSAL / ADJUSTMENT
QUANTITY          INT           -- 출고·폐기는 음수
REASON            VARCHAR(100)  -- Hold 사유, 조정 사유 등
CREATED_AT        DATETIME
```

---

## 핵심 Enum 설계

### StockStatus
```java
public enum StockStatus {
    PENDING_INSPECTION,  // 검사 대기 (입고 직후 초기 상태)
    AVAILABLE,           // 출고 가능
    WARNING,             // 유통기한 20% 이하 임박
    HOLD,                // 격리 (온도 이탈 / 품질 이슈 / 검사 부적합)
    EXPIRED,             // 유통기한 만료
    DISPOSED;            // 폐기 확정

    public boolean canTransitionTo(StockStatus next) {
        return switch (this) {
            case PENDING_INSPECTION -> next == AVAILABLE || next == HOLD;
            case AVAILABLE          -> next == HOLD || next == WARNING || next == EXPIRED;
            case WARNING            -> next == HOLD || next == EXPIRED;
            case HOLD               -> next == AVAILABLE || next == DISPOSED;
            case EXPIRED, DISPOSED  -> false;
        };
    }
}
```

### HoldReason
```java
public enum HoldReason {
    TEMPERATURE_DEVIATION,   // 온도 이탈
    INSPECTION_REQUIRED,     // 검사 대기
    FAILED_INSPECTION,       // 검사 부적합
    QUALITY_ISSUE,           // 품질 이슈
    RECALL,                  // 리콜
    PEST_CONTAMINATION       // 이물질 / 오염
}
```

### MovementType
```java
public enum MovementType {
    INBOUND,      // 입고
    OUTBOUND,     // 출고
    HOLD,         // 격리
    RELEASE,      // 격리 해제
    DISPOSAL,     // 폐기
    ADJUSTMENT    // 수량 조정 (실물 불일치 보정)
}
```

---

## 주요 기능 흐름

### 입고 흐름
```
입고 데이터 입력 (LOT_NO, 제조일자, 유통기한, 수량, 입고 구역 온도)
  ├─ 온도 적합성 검증
  │    ├─ 적합 → STOCK_STATUS = PENDING_INSPECTION (검사 대기)
  │    └─ 부적합 → STOCK_STATUS = HOLD (자동 격리)
  └─ STOCK_MOVEMENT 이력 기록 (INBOUND)
```

### FEFO 출고 흐름
```
출고 요청 (PRODUCT_NO, 요청 수량)
  ├─ AVAILABLE 또는 WARNING 상태 배치만 조회
  ├─ EXPIRY_DATE 오름차순 정렬
  ├─ 수량 충족될 때까지 순서대로 차감
  └─ STOCK_MOVEMENT 이력 기록 (OUTBOUND)
```

### 배치 스케줄러
```
매일 자정 실행
  ├─ EXPIRY_DATE 기준 잔여 유통기한 20% 이하 → WARNING 상태 전이
  └─ EXPIRY_DATE 지난 배치 → EXPIRED 상태 전이
```

---

## 메뉴 구조

| 메뉴 | 기능 |
|------|------|
| 상품 관리 | 등록 / 수정 / 삭제 / 목록 조회 |
| 재고 관리 | 입고 등록 / 재고 현황 / 배치별 상세 / Hold 처리 |
| 출고 관리 | 출고 요청 → FEFO 순서 확인 → 출고 확정 |
| 이력 조회 | STOCK_MOVEMENT 조회 (기간 / 상품 / 이동 타입 필터) |

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| DB | MariaDB |
| 배치 | Spring Scheduler (`@Scheduled`) |
| 내부 이벤트 | Spring `ApplicationEventPublisher` |
| 빌드 | Gradle |

---

## 면접 대비 설계 결정 근거

| 질문 | 답변 요약 |
|------|-----------|
| Hold 시 수량을 왜 차감 안 했나요? | 폐기 확정 전 물리적 재고는 존재함. 상태 전이로 관리해야 이력 추적과 롤백이 용이 |
| enum을 왜 사용했나요? | 잘못된 상태 전이를 컴파일 시점에 차단. String 관리 시 오타·불일치 런타임 버그 위험 |
| FEFO를 왜 DB 쿼리로 구현했나요? | 대용량 재고 데이터를 애플리케이션 레이어에서 정렬하면 메모리 부담. DB 인덱스 활용이 효율적 |
| 입고 초기 상태를 왜 AVAILABLE이 아닌 PENDING_INSPECTION으로 했나요? | 실제 식품 유통 프로세스상 검사 적합 판정 전 출하 불가. 전 직장 경험에서 도출한 도메인 규칙 |
| STOCK_MOVEMENT 테이블을 왜 별도로 뒀나요? | 재고 수량 변경 이유를 추적하기 위함. "이 재고가 왜 줄었지?" 는 현업에서 가장 빈번한 질문 |
