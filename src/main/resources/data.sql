INSERT INTO permission (id, name, created_at, updated_at) VALUES
    (1, 'OPERATOR_DASHBOARD', NOW(6), NOW(6)),
    (2, 'OPERATOR_MEMBER_READ', NOW(6), NOW(6)),
    (3, 'OPERATOR_MEMBER_WRITE', NOW(6), NOW(6)),
    (4, 'OPERATOR_PRODUCT_READ', NOW(6), NOW(6)),
    (5, 'OPERATOR_PRODUCT_WRITE', NOW(6), NOW(6)),
    (6, 'OPERATOR_ORDER_READ', NOW(6), NOW(6)),
    (7, 'OPERATOR_ORDER_WRITE', NOW(6), NOW(6)),
    (8, 'OPERATOR_CURATION_READ', NOW(6), NOW(6)),
    (9, 'OPERATOR_CURATION_WRITE', NOW(6), NOW(6)),
    (10, 'OPERATOR_SYSTEM_READ', NOW(6), NOW(6)),
    (11, 'OPERATOR_SYSTEM_WRITE', NOW(6), NOW(6)),
    (12, 'PARTNER_DASHBOARD', NOW(6), NOW(6)),
    (13, 'PARTNER_PRODUCT_READ', NOW(6), NOW(6)),
    (14, 'PARTNER_PRODUCT_WRITE', NOW(6), NOW(6)),
    (15, 'PARTNER_ORDER_READ', NOW(6), NOW(6)),
    (16, 'PARTNER_CURATION_READ', NOW(6), NOW(6)),
    (17, 'PARTNER_CURATION_WRITE', NOW(6), NOW(6)),
    (18, 'PARTNER_SETTLEMENT_READ', NOW(6), NOW(6));

INSERT INTO role (id, name, created_at, updated_at) VALUES
    (1, 'OPERATOR', NOW(6), NOW(6)),
    (2, 'OPERATOR_CS', NOW(6), NOW(6)),
    (3, 'PARTNER', NOW(6), NOW(6)),
    (4, 'PARTNER_STAFF', NOW(6), NOW(6)),
    (5, 'CUSTOMER', NOW(6), NOW(6));

INSERT INTO role_permission (role_id, permission_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
    (1, 7), (1, 8), (1, 9), (1, 10), (1, 11);

INSERT INTO role_permission (role_id, permission_id) VALUES
    (2, 1), (2, 2), (2, 6), (2, 7);

INSERT INTO role_permission (role_id, permission_id) VALUES
    (3, 12), (3, 13), (3, 14), (3, 15), (3, 16), (3, 17), (3, 18);

INSERT INTO role_permission (role_id, permission_id) VALUES
    (4, 12), (4, 13), (4, 15);

INSERT INTO menu (id, name, path, sort_order, parent_id, created_at, updated_at) VALUES
    (1, '대시보드', '/dashboard', 1, NULL, NOW(6), NOW(6)),
    (2, '상품관리', NULL, 2, NULL, NOW(6), NOW(6)),
    (3, '주문관리', NULL, 3, NULL, NOW(6), NOW(6)),
    (4, '회원관리', NULL, 4, NULL, NOW(6), NOW(6)),
    (5, '큐레이션관리', NULL, 5, NULL, NOW(6), NOW(6)),
    (6, '시스템관리', NULL, 6, NULL, NOW(6), NOW(6));

INSERT INTO menu (id, name, path, sort_order, parent_id, created_at, updated_at) VALUES
    (7, '상품목록', '/products', 1, 2, NOW(6), NOW(6)),
    (8, '항공권관리', '/products/flights', 2, 2, NOW(6), NOW(6)),
    (9, '숙박관리', '/products/accommodations', 3, 2, NOW(6), NOW(6)),
    (10, '티켓관리', '/products/tickets', 4, 2, NOW(6), NOW(6)),
    (11, '주문목록', '/orders', 1, 3, NOW(6), NOW(6)),
    (12, '주문통계', '/orders/statistics', 2, 3, NOW(6), NOW(6)),
    (13, '회원목록', '/members', 1, 4, NOW(6), NOW(6)),
    (14, '회원통계', '/members/statistics', 2, 4, NOW(6), NOW(6)),
    (15, '큐레이션목록', '/curations', 1, 5, NOW(6), NOW(6)),
    (16, '큐레이션생성', '/curations/new', 2, 5, NOW(6), NOW(6)),
    (17, '권한관리', '/system/permissions', 1, 6, NOW(6), NOW(6)),
    (18, '역할관리', '/system/roles', 2, 6, NOW(6), NOW(6)),
    (19, '메뉴관리', '/system/menus', 3, 6, NOW(6), NOW(6));

INSERT INTO permission_menu (permission_id, menu_id) VALUES
    (1, 1), (12, 1);

INSERT INTO permission_menu (permission_id, menu_id) VALUES
    (4, 2), (4, 7), (4, 8), (4, 9), (4, 10),
    (5, 2), (5, 7), (5, 8), (5, 9), (5, 10),
    (13, 2), (13, 7), (13, 8), (13, 9), (13, 10),
    (14, 2), (14, 7), (14, 8), (14, 9), (14, 10);

INSERT INTO permission_menu (permission_id, menu_id) VALUES
    (6, 3), (6, 11), (6, 12),
    (7, 3), (7, 11), (7, 12),
    (15, 3), (15, 11), (15, 12);

INSERT INTO permission_menu (permission_id, menu_id) VALUES
    (2, 4), (2, 13), (2, 14),
    (3, 4), (3, 13), (3, 14);

INSERT INTO permission_menu (permission_id, menu_id) VALUES
    (8, 5), (8, 15),
    (9, 5), (9, 15), (9, 16),
    (16, 5), (16, 15),
    (17, 5), (17, 15), (17, 16);

INSERT INTO permission_menu (permission_id, menu_id) VALUES
    (10, 6), (10, 17), (10, 18), (10, 19),
    (11, 6), (11, 17), (11, 18), (11, 19);

INSERT INTO member (id, email, password, name, phone, birth_date, type, status, created_at, updated_at) VALUES
    (1, 'operator@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Operator User', '010-1234-5678', '1990-01-01', 'OPERATOR', 'ACTIVE', NOW(6), NOW(6)),
    (2, 'operator.cs@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'CS Manager', '010-1111-2222', '1992-03-15', 'OPERATOR', 'ACTIVE', NOW(6), NOW(6)),
    (3, 'partner@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Partner User', '010-2345-6789', '1992-05-15', 'PARTNER', 'ACTIVE', NOW(6), NOW(6)),
    (4, 'partner.staff@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Partner Staff', '010-3333-4444', '1995-08-20', 'PARTNER', 'ACTIVE', NOW(6), NOW(6)),
    (5, 'customer@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Customer User', '010-3456-7890', '1995-10-20', 'CUSTOMER', 'ACTIVE', NOW(6), NOW(6));

INSERT INTO member_role (member_id, role_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);

INSERT INTO product (id, category, name, description, price, quantity, created_by, updated_by, created_at, updated_at) VALUES
    (1, 'FLIGHT', '인천-도쿄 왕복', '대한항공 직항 이코노미 클래스', 450000, 100, 1, 1, NOW(6), NOW(6)),
    (2, 'FLIGHT', '김포-오사카 왕복', '아시아나항공 직항 비즈니스 클래스', 850000, 50, 1, 1, NOW(6), NOW(6)),
    (3, 'FLIGHT', '인천-방콕 왕복', '타이항공 직항 이코노미 클래스', 520000, 80, 1, 1, NOW(6), NOW(6)),
    (4, 'FLIGHT', '인천-싱가포르 왕복', '싱가포르항공 직항 프리미엄 이코노미', 780000, 60, 1, 1, NOW(6), NOW(6)),
    (5, 'FLIGHT', '인천-하노이 왕복', '베트남항공 직항 이코노미 클래스', 380000, 120, 1, 1, NOW(6), NOW(6)),
    (6, 'FLIGHT', '인천-홍콩 왕복', '캐세이퍼시픽 직항 비즈니스 클래스', 920000, 40, 1, 1, NOW(6), NOW(6)),
    (7, 'FLIGHT', '인천-LA 왕복', '대한항공 직항 이코노미 클래스', 1250000, 70, 1, 1, NOW(6), NOW(6)),
    (8, 'FLIGHT', '인천-파리 왕복', '에어프랑스 직항 비즈니스 클래스', 3200000, 30, 1, 1, NOW(6), NOW(6)),
    (9, 'ACCOMMODATION', '신라호텔 서울', '명동 5성급 호텔 디럭스룸 1박', 350000, 30, 1, 1, NOW(6), NOW(6)),
    (10, 'ACCOMMODATION', '제주 신라호텔', '제주 5성급 호텔 오션뷰 2박', 680000, 20, 1, 1, NOW(6), NOW(6)),
    (11, 'ACCOMMODATION', '롯데호텔 부산', '해운대 5성급 호텔 스위트룸 1박', 420000, 25, 1, 1, NOW(6), NOW(6)),
    (12, 'ACCOMMODATION', '파라다이스시티 인천', '인천 복합리조트 디럭스룸 1박', 280000, 50, 1, 1, NOW(6), NOW(6)),
    (13, 'ACCOMMODATION', '한화리조트 설악', '설악산 리조트 패밀리룸 2박', 320000, 40, 1, 1, NOW(6), NOW(6)),
    (14, 'ACCOMMODATION', '그랜드하얏트 서울', '남산뷰 5성급 호텔 클럽룸 1박', 480000, 20, 1, 1, NOW(6), NOW(6)),
    (15, 'ACCOMMODATION', '웨스틴조선 부산', '광안리뷰 5성급 호텔 프리미어룸 1박', 380000, 35, 1, 1, NOW(6), NOW(6)),
    (16, 'ACCOMMODATION', '반얀트리 클럽 앤 스파 서울', '남산 럭셔리 스파 호텔 스위트 1박', 750000, 15, 1, 1, NOW(6), NOW(6)),
    (17, 'TICKET', '뮤지컬 오페라의 유령', 'VIP석 평일 공연', 150000, 200, 1, 1, NOW(6), NOW(6)),
    (18, 'TICKET', '뮤지컬 레미제라블', 'R석 주말 공연', 130000, 150, 1, 1, NOW(6), NOW(6)),
    (19, 'TICKET', '뮤지컬 위키드', 'S석 주말 공연', 140000, 180, 1, 1, NOW(6), NOW(6)),
    (20, 'TICKET', '뮤지컬 시카고', 'VIP석 평일 공연', 160000, 120, 1, 1, NOW(6), NOW(6)),
    (21, 'TICKET', '에버랜드 자유이용권', '성인 종일권', 62000, 500, 1, 1, NOW(6), NOW(6)),
    (22, 'TICKET', '롯데월드 자유이용권', '성인 종일권', 59000, 500, 1, 1, NOW(6), NOW(6)),
    (23, 'TICKET', '서울랜드 자유이용권', '성인 종일권', 42000, 400, 1, 1, NOW(6), NOW(6)),
    (24, 'TICKET', '아쿠아플라넷 제주', '성인 입장권', 41000, 300, 1, 1, NOW(6), NOW(6)),
    (25, 'TICKET', '국립중앙박물관 특별전', '성인 입장권', 15000, 1000, 1, 1, NOW(6), NOW(6)),
    (26, 'TICKET', '난타 공연', 'VIP석', 70000, 250, 1, 1, NOW(6), NOW(6));

INSERT INTO flight (id, airline, flight_number, departure_airport, arrival_airport, departure_time, arrival_time) VALUES
    (1, '대한항공', 'KE001', '인천국제공항', '나리타국제공항', '2025-03-01 10:00:00', '2025-03-01 12:30:00'),
    (2, '아시아나항공', 'OZ101', '김포국제공항', '간사이국제공항', '2025-03-15 14:00:00', '2025-03-15 16:00:00'),
    (3, '타이항공', 'TG657', '인천국제공항', '수완나품국제공항', '2025-04-01 09:00:00', '2025-04-01 13:00:00'),
    (4, '싱가포르항공', 'SQ601', '인천국제공항', '창이국제공항', '2025-04-10 11:00:00', '2025-04-10 16:30:00'),
    (5, '베트남항공', 'VN417', '인천국제공항', '노이바이국제공항', '2025-04-20 08:00:00', '2025-04-20 11:30:00'),
    (6, '캐세이퍼시픽', 'CX419', '인천국제공항', '홍콩국제공항', '2025-05-01 13:00:00', '2025-05-01 16:00:00'),
    (7, '대한항공', 'KE017', '인천국제공항', '로스앤젤레스국제공항', '2025-05-15 10:00:00', '2025-05-15 06:00:00'),
    (8, '에어프랑스', 'AF267', '인천국제공항', '샤를드골국제공항', '2025-06-01 12:00:00', '2025-06-01 18:30:00');

INSERT INTO accommodation (id, place, check_in_time, check_out_time) VALUES
    (9, '서울특별시 중구 동호로 249', '2025-04-01 15:00:00', '2025-04-02 11:00:00'),
    (10, '제주특별자치도 서귀포시 색달동 3039-3', '2025-05-01 15:00:00', '2025-05-03 11:00:00'),
    (11, '부산광역시 해운대구 해운대해변로 30', '2025-04-15 15:00:00', '2025-04-16 11:00:00'),
    (12, '인천광역시 중구 영종해안남로 321', '2025-04-20 15:00:00', '2025-04-21 11:00:00'),
    (13, '강원도 속초시 설악산로 1456', '2025-05-10 15:00:00', '2025-05-12 11:00:00'),
    (14, '서울특별시 용산구 소월로 322', '2025-05-20 15:00:00', '2025-05-21 11:00:00'),
    (15, '부산광역시 수영구 광안해변로 268', '2025-06-01 15:00:00', '2025-06-02 11:00:00'),
    (16, '서울특별시 중구 장충동2가 산5-5', '2025-06-15 15:00:00', '2025-06-16 11:00:00');

INSERT INTO ticket (id, place, performance_time, duration, age_limit) VALUES
    (17, '블루스퀘어 신한카드홀', '2025-06-15 19:00:00', '2시간 40분', '8세 이상'),
    (18, '샤롯데씨어터', '2025-07-20 14:00:00', '3시간', '8세 이상'),
    (19, '블루스퀨어 인터파크홀', '2025-08-01 19:30:00', '2시간 50분', '8세 이상'),
    (20, '예술의전당 오페라극장', '2025-08-15 19:00:00', '2시간 30분', '7세 이상'),
    (21, '경기도 용인시 처인구 포곡읍', '2025-03-01 10:00:00', '종일', '전체 이용가'),
    (22, '서울특별시 송파구 잠실동', '2025-03-01 10:00:00', '종일', '전체 이용가'),
    (23, '경기도 과천시 막계동', '2025-03-01 10:00:00', '종일', '전체 이용가'),
    (24, '제주특별자치도 서귀포시 성산읍', '2025-03-01 09:30:00', '3시간', '전체 이용가'),
    (25, '서울특별시 용산구 서빙고로 137', '2025-04-01 10:00:00', '2시간', '전체 이용가'),
    (26, '서울특별시 중구 명동길 26', '2025-03-15 17:00:00', '1시간 40분', '전체 이용가');

INSERT INTO curation (id, title, is_exposed, sort_order, created_by, updated_by, created_at, updated_at) VALUES
    (1, '여름 휴가 특선', true, 1, 1, 1, NOW(6), NOW(6)),
    (2, '가을 문화 여행', true, 2, 1, 1, NOW(6), NOW(6)),
    (3, '겨울 특가 패키지', false, 3, 1, 1, NOW(6), NOW(6));

INSERT INTO curation_product (curation_id, product_id, sort_order) VALUES
    (1, 1, 1),
    (1, 3, 2),
    (2, 5, 1),
    (2, 6, 2),
    (3, 2, 1),
    (3, 4, 2);

ALTER TABLE permission AUTO_INCREMENT = 51;
ALTER TABLE role AUTO_INCREMENT = 51;
ALTER TABLE menu AUTO_INCREMENT = 51;
ALTER TABLE member AUTO_INCREMENT = 51;
ALTER TABLE product AUTO_INCREMENT = 51;
ALTER TABLE curation AUTO_INCREMENT = 51;
ALTER TABLE order_product AUTO_INCREMENT = 51;
ALTER TABLE curation_product AUTO_INCREMENT = 51;