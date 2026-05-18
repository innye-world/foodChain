-- DBeaver에서 CREATED_AT / MODIFIED_AT 로 추가한 경우 JPA 엔티티(created_at, modified_at)와 맞추기
ALTER TABLE product
  CHANGE COLUMN CREATED_AT created_at datetime(6) NOT NULL DEFAULT current_timestamp(6),
  CHANGE COLUMN MODIFIED_AT modified_at datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6);
