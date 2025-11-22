SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS curation_product;
DROP TABLE IF EXISTS curation;
DROP TABLE IF EXISTS order_product;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS accommodation;
DROP TABLE IF EXISTS flight;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS permission_menu;
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS member_permission;
DROP TABLE IF EXISTS member_role;
DROP TABLE IF EXISTS menu;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS member;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    type VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT chk_member_type CHECK (type IN ('OPERATOR', 'PARTNER', 'CUSTOMER'))
);

CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    path VARCHAR(255),
    sort_order BIGINT,
    parent_id BIGINT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_menu_parent FOREIGN KEY (parent_id) REFERENCES menu(id)
);

CREATE TABLE member_role (
    member_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (member_id, role_id),
    CONSTRAINT fk_member_role_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_member_role_role FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE member_permission (
    member_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (member_id, permission_id),
    CONSTRAINT fk_member_permission_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_member_permission_permission FOREIGN KEY (permission_id) REFERENCES permission(id)
);

CREATE TABLE role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permission(id)
);

CREATE TABLE permission_menu (
    permission_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (permission_id, menu_id),
    CONSTRAINT fk_permission_menu_permission FOREIGN KEY (permission_id) REFERENCES permission(id),
    CONSTRAINT fk_permission_menu_menu FOREIGN KEY (menu_id) REFERENCES menu(id)
);

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT chk_category CHECK (category IN ('FLIGHT', 'ACCOMMODATION', 'TICKET'))
);

CREATE TABLE flight (
    id BIGINT NOT NULL PRIMARY KEY,
    airline VARCHAR(255) NOT NULL,
    flight_number VARCHAR(255) NOT NULL,
    departure_airport VARCHAR(255) NOT NULL,
    arrival_airport VARCHAR(255) NOT NULL,
    departure_time DATETIME(6) NOT NULL,
    arrival_time DATETIME(6) NOT NULL,
    CONSTRAINT fk_flight_product FOREIGN KEY (id) REFERENCES product(id)
);

CREATE TABLE accommodation (
    id BIGINT NOT NULL PRIMARY KEY,
    place VARCHAR(255) NOT NULL,
    check_in_time DATETIME(6) NOT NULL,
    check_out_time DATETIME(6) NOT NULL,
    CONSTRAINT fk_accommodation_product FOREIGN KEY (id) REFERENCES product(id)
);

CREATE TABLE ticket (
    id BIGINT NOT NULL PRIMARY KEY,
    place VARCHAR(255) NOT NULL,
    performance_time DATETIME(6) NOT NULL,
    duration VARCHAR(255) NOT NULL,
    age_limit VARCHAR(255) NOT NULL,
    CONSTRAINT fk_ticket_product FOREIGN KEY (id) REFERENCES product(id)
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ordered_at DATETIME(6) NOT NULL,
    status VARCHAR(255) NOT NULL,
    member_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_orders_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT chk_order_status CHECK (status IN ('ORDER', 'CONFIRM', 'CANCEL'))
);

CREATE TABLE order_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_price BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_order_product_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_product_product FOREIGN KEY (product_id) REFERENCES product(id)
);

CREATE TABLE curation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    is_exposed BIT(1) NOT NULL,
    sort_order BIGINT NOT NULL,
    created_by BIGINT,
    updated_by BIGINT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE curation_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sort_order BIGINT,
    curation_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_curation_product_curation FOREIGN KEY (curation_id) REFERENCES curation(id),
    CONSTRAINT fk_curation_product_product FOREIGN KEY (product_id) REFERENCES product(id)
);