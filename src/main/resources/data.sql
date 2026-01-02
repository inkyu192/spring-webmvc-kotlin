INSERT INTO permission (id, name, created_at, updated_at)
VALUES (1, 'USER_READ', NOW(6), NOW(6)),
       (2, 'USER_WRITE', NOW(6), NOW(6)),
       (3, 'PRODUCT_READ', NOW(6), NOW(6)),
       (4, 'PRODUCT_WRITE', NOW(6), NOW(6)),
       (5, 'ORDER_READ', NOW(6), NOW(6)),
       (6, 'ORDER_WRITE', NOW(6), NOW(6)),
       (8, 'CURATION_READ', NOW(6), NOW(6)),
       (9, 'CURATION_WRITE', NOW(6), NOW(6));

INSERT INTO role (id, name, created_at, updated_at)
VALUES (1, 'OPERATOR', NOW(6), NOW(6)),
       (2, 'OPERATOR_CS', NOW(6), NOW(6)),
       (3, 'PARTNER', NOW(6), NOW(6)),
       (4, 'PARTNER_STAFF', NOW(6), NOW(6)),
       (5, 'CUSTOMER', NOW(6), NOW(6));

INSERT INTO role_permission (role_id, permission_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 8),
       (1, 9),
       (2, 1),
       (2, 2),
       (2, 5),
       (2, 6),
       (3, 3),
       (3, 4),
       (3, 5),
       (3, 6),
       (3, 8),
       (3, 9),
       (4, 3),
       (4, 5);

INSERT INTO menu (id, name, path, sort_order, parent_id, created_at, updated_at)
VALUES (1, '회원', NULL, 1, NULL, NOW(6), NOW(6)),
       (2, '상품', NULL, 2, NULL, NOW(6), NOW(6)),
       (3, '주문', NULL, 3, NULL, NOW(6), NOW(6)),
       (4, '큐레이션', NULL, 4, NULL, NOW(6), NOW(6));

INSERT INTO menu (id, name, path, sort_order, parent_id, created_at, updated_at)
VALUES (5, '회원관리', '/users', 1, 1, NOW(6), NOW(6)),
       (6, '교통수단관리', '/products/transports', 1, 2, NOW(6), NOW(6)),
       (7, '숙박관리', '/products/accommodations', 2, 2, NOW(6), NOW(6)),
       (9, '주문관리', '/orders', 1, 3, NOW(6), NOW(6)),
       (10, '큐레이션관리', '/curations', 1, 4, NOW(6), NOW(6));

INSERT INTO permission_menu (permission_id, menu_id)
VALUES (1, 1),
       (1, 5),
       (2, 1),
       (2, 5),
       (3, 2),
       (3, 6),
       (3, 7),
       (4, 2),
       (4, 6),
       (4, 7),
       (5, 3),
       (5, 9),
       (6, 3),
       (6, 9),
       (8, 4),
       (8, 10),
       (9, 4),
       (9, 10);

INSERT INTO company (id, name, business_number, address, phone_number, created_at, updated_at)
VALUES (1, '테크 코퍼레이션', '123-45-67890', '서울특별시 강남구 테헤란로 123', '02-1234-5678', NOW(6), NOW(6)),
       (2, '글로벌 파트너스', '234-56-78901', '서울특별시 서초구 서초대로 456', '02-2345-6789', NOW(6), NOW(6)),
       (3, '이노베이션 그룹', '345-67-89012', '경기도 성남시 분당구 판교역로 789', '031-3456-7890', NOW(6), NOW(6)),
       (4, '비즈니스 솔루션', '456-78-90123', '서울특별시 마포구 월드컵북로 321', '02-4567-8901', NOW(6), NOW(6)),
       (5, '엔터프라이즈 주식회사', '567-89-01234', '부산광역시 해운대구 센텀중앙로 654', '051-5678-9012', NOW(6), NOW(6));

INSERT INTO product (id, category, name, description, price, quantity, created_by, updated_by, created_at, updated_at)
VALUES (1, 'TRANSPORT', '인천-도쿄 왕복', '대한항공 직항 이코노미 클래스', 450000, 100, 1, 1, NOW(6), NOW(6)),
       (2, 'TRANSPORT', '김포-오사카 왕복', '아시아나항공 직항 비즈니스 클래스', 850000, 50, 1, 1, NOW(6), NOW(6)),
       (3, 'TRANSPORT', '인천-방콕 왕복', '타이항공 직항 이코노미 클래스', 520000, 80, 1, 1, NOW(6), NOW(6)),
       (4, 'TRANSPORT', '인천-싱가포르 왕복', '싱가포르항공 직항 프리미엄 이코노미', 780000, 60, 1, 1, NOW(6), NOW(6)),
       (5, 'TRANSPORT', '인천-하노이 왕복', '베트남항공 직항 이코노미 클래스', 380000, 120, 1, 1, NOW(6), NOW(6)),
       (6, 'TRANSPORT', '인천-홍콩 왕복', '캐세이퍼시픽 직항 비즈니스 클래스', 920000, 40, 1, 1, NOW(6), NOW(6)),
       (7, 'TRANSPORT', '인천-LA 왕복', '대한항공 직항 이코노미 클래스', 1250000, 70, 1, 1, NOW(6), NOW(6)),
       (8, 'TRANSPORT', '인천-파리 왕복', '에어프랑스 직항 비즈니스 클래스', 3200000, 30, 1, 1, NOW(6), NOW(6)),
       (9, 'ACCOMMODATION', '신라호텔 서울', '명동 5성급 호텔 디럭스룸 1박', 350000, 30, 1, 1, NOW(6), NOW(6)),
       (10, 'ACCOMMODATION', '제주 신라호텔', '제주 5성급 호텔 오션뷰 2박', 680000, 20, 1, 1, NOW(6), NOW(6)),
       (11, 'ACCOMMODATION', '롯데호텔 부산', '해운대 5성급 호텔 스위트룸 1박', 420000, 25, 1, 1, NOW(6), NOW(6)),
       (12, 'ACCOMMODATION', '파라다이스시티 인천', '인천 복합리조트 디럭스룸 1박', 280000, 50, 1, 1, NOW(6), NOW(6)),
       (13, 'ACCOMMODATION', '한화리조트 설악', '설악산 리조트 패밀리룸 2박', 320000, 40, 1, 1, NOW(6), NOW(6)),
       (14, 'ACCOMMODATION', '그랜드하얏트 서울', '남산뷰 5성급 호텔 클럽룸 1박', 480000, 20, 1, 1, NOW(6), NOW(6)),
       (15, 'ACCOMMODATION', '웨스틴조선 부산', '광안리뷰 5성급 호텔 프리미어룸 1박', 380000, 35, 1, 1, NOW(6), NOW(6)),
       (16, 'ACCOMMODATION', '반얀트리 클럽 앤 스파 서울', '남산 럭셔리 스파 호텔 스위트 1박', 750000, 15, 1, 1, NOW(6), NOW(6));

INSERT INTO transport (product_id, departure_location, arrival_location, departure_time, arrival_time)
VALUES (1, '인천국제공항', '나리타국제공항', '2025-03-01 10:00:00', '2025-03-01 12:30:00'),
       (2, '김포국제공항', '간사이국제공항', '2025-03-15 14:00:00', '2025-03-15 16:00:00'),
       (3, '인천국제공항', '수완나품국제공항', '2025-04-01 09:00:00', '2025-04-01 13:00:00'),
       (4, '인천국제공항', '창이국제공항', '2025-04-10 11:00:00', '2025-04-10 16:30:00'),
       (5, '인천국제공항', '노이바이국제공항', '2025-04-20 08:00:00', '2025-04-20 11:30:00'),
       (6, '인천국제공항', '홍콩국제공항', '2025-05-01 13:00:00', '2025-05-01 16:00:00'),
       (7, '인천국제공항', '로스앤젤레스국제공항', '2025-05-15 10:00:00', '2025-05-15 06:00:00'),
       (8, '인천국제공항', '샤를드골국제공항', '2025-06-01 12:00:00', '2025-06-01 18:30:00');

INSERT INTO accommodation (product_id, place, check_in_time, check_out_time)
VALUES (9, '서울특별시 중구 동호로 249', '2025-04-01 15:00:00', '2025-04-02 11:00:00'),
       (10, '제주특별자치도 서귀포시 색달동 3039-3', '2025-05-01 15:00:00', '2025-05-03 11:00:00'),
       (11, '부산광역시 해운대구 해운대해변로 30', '2025-04-15 15:00:00', '2025-04-16 11:00:00'),
       (12, '인천광역시 중구 영종해안남로 321', '2025-04-20 15:00:00', '2025-04-21 11:00:00'),
       (13, '강원도 속초시 설악산로 1456', '2025-05-10 15:00:00', '2025-05-12 11:00:00'),
       (14, '서울특별시 용산구 소월로 322', '2025-05-20 15:00:00', '2025-05-21 11:00:00'),
       (15, '부산광역시 수영구 광안해변로 268', '2025-06-01 15:00:00', '2025-06-02 11:00:00'),
       (16, '서울특별시 중구 장충동2가 산5-5', '2025-06-15 15:00:00', '2025-06-16 11:00:00');

INSERT INTO curation (id, title, category, is_exposed, sort_order, created_by, updated_by, created_at, updated_at)
VALUES (1, '여름 휴가 특선', 'HOME', true, 1, 1, 1, NOW(6), NOW(6)),
       (2, '가을 문화 여행', 'EVENT', true, 2, 1, 1, NOW(6), NOW(6)),
       (3, '겨울 특가 패키지', 'PROMOTION', false, 3, 1, 1, NOW(6), NOW(6));

INSERT INTO curation_product (curation_id, product_id, sort_order)
VALUES (1, 1, 1),
       (1, 3, 2),
       (2, 5, 1),
       (2, 6, 2),
       (3, 2, 1),
       (3, 4, 2);

ALTER TABLE permission AUTO_INCREMENT = 51;
ALTER TABLE role AUTO_INCREMENT = 51;
ALTER TABLE menu AUTO_INCREMENT = 51;
ALTER TABLE company AUTO_INCREMENT = 51;
ALTER TABLE delivery_address AUTO_INCREMENT = 51;
ALTER TABLE product AUTO_INCREMENT = 51;
ALTER TABLE curation AUTO_INCREMENT = 51;
ALTER TABLE order_product AUTO_INCREMENT = 51;
ALTER TABLE curation_product AUTO_INCREMENT = 51;