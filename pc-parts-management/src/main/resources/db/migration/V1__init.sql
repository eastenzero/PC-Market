SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE sys_user (
  id bigint NOT NULL AUTO_INCREMENT,
  username varchar(64) NOT NULL,
  password varchar(255) NOT NULL,
  nickname varchar(64) DEFAULT NULL,
  phone varchar(32) DEFAULT NULL,
  email varchar(128) DEFAULT NULL,
  status tinyint NOT NULL DEFAULT 1,
  last_login_at datetime DEFAULT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_username_deleted (username, deleted),
  KEY idx_sys_user_phone (phone),
  KEY idx_sys_user_email (email)
);

CREATE TABLE sys_role (
  id bigint NOT NULL AUTO_INCREMENT,
  code varchar(64) NOT NULL,
  name varchar(64) NOT NULL,
  description varchar(255) DEFAULT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_code_deleted (code, deleted)
);

CREATE TABLE sys_permission (
  id bigint NOT NULL AUTO_INCREMENT,
  code varchar(128) NOT NULL,
  name varchar(128) NOT NULL,
  type tinyint NOT NULL DEFAULT 1,
  parent_id bigint DEFAULT NULL,
  path varchar(255) DEFAULT NULL,
  sort_order int NOT NULL DEFAULT 0,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_permission_code_deleted (code, deleted),
  KEY idx_sys_permission_parent_id (parent_id),
  CONSTRAINT fk_sys_permission_parent_id FOREIGN KEY (parent_id) REFERENCES sys_permission (id)
);

CREATE TABLE sys_user_role (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_role_user_id_role_id_deleted (user_id, role_id, deleted),
  KEY idx_sys_user_role_role_id (role_id),
  CONSTRAINT fk_sys_user_role_user_id FOREIGN KEY (user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_sys_user_role_role_id FOREIGN KEY (role_id) REFERENCES sys_role (id)
);

CREATE TABLE sys_role_permission (
  id bigint NOT NULL AUTO_INCREMENT,
  role_id bigint NOT NULL,
  permission_id bigint NOT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_permission_role_id_permission_id_deleted (role_id, permission_id, deleted),
  KEY idx_sys_role_permission_permission_id (permission_id),
  CONSTRAINT fk_sys_role_permission_role_id FOREIGN KEY (role_id) REFERENCES sys_role (id),
  CONSTRAINT fk_sys_role_permission_permission_id FOREIGN KEY (permission_id) REFERENCES sys_permission (id)
);

CREATE TABLE brand (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(64) NOT NULL,
  description varchar(255) DEFAULT NULL,
  status tinyint NOT NULL DEFAULT 1,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_brand_name_deleted (name, deleted),
  KEY idx_brand_name (name)
);

CREATE TABLE product_category (
  id bigint NOT NULL AUTO_INCREMENT,
  parent_id bigint DEFAULT NULL,
  name varchar(64) NOT NULL,
  sort_order int NOT NULL DEFAULT 0,
  level int NOT NULL DEFAULT 1,
  path varchar(255) DEFAULT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_product_category_parent_id_name_deleted (parent_id, name, deleted),
  KEY idx_product_category_parent_id (parent_id),
  CONSTRAINT fk_product_category_parent_id FOREIGN KEY (parent_id) REFERENCES product_category (id)
);

CREATE TABLE product (
  id bigint NOT NULL AUTO_INCREMENT,
  spu_code varchar(64) NOT NULL,
  name varchar(128) NOT NULL,
  category_id bigint NOT NULL,
  brand_id bigint NOT NULL,
  description text,
  status tinyint NOT NULL DEFAULT 1,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_product_spu_code_deleted (spu_code, deleted),
  KEY idx_product_name (name),
  KEY idx_product_category_id (category_id),
  KEY idx_product_brand_id (brand_id),
  CONSTRAINT fk_product_category_id FOREIGN KEY (category_id) REFERENCES product_category (id),
  CONSTRAINT fk_product_brand_id FOREIGN KEY (brand_id) REFERENCES brand (id)
);

CREATE TABLE product_sku (
  id bigint NOT NULL AUTO_INCREMENT,
  product_id bigint NOT NULL,
  sku_code varchar(64) NOT NULL,
  barcode varchar(64) DEFAULT NULL,
  name varchar(128) NOT NULL,
  spec_json json DEFAULT NULL,
  price decimal(12,2) NOT NULL DEFAULT 0.00,
  cost decimal(12,2) NOT NULL DEFAULT 0.00,
  status tinyint NOT NULL DEFAULT 1,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_product_sku_sku_code_deleted (sku_code, deleted),
  KEY idx_product_sku_product_id (product_id),
  KEY idx_product_sku_barcode (barcode),
  KEY idx_product_sku_name (name),
  CONSTRAINT fk_product_sku_product_id FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE TABLE customer (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(128) NOT NULL,
  phone varchar(32) DEFAULT NULL,
  email varchar(128) DEFAULT NULL,
  address varchar(255) DEFAULT NULL,
  status tinyint NOT NULL DEFAULT 1,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_customer_name_deleted (name, deleted),
  KEY idx_customer_phone (phone),
  KEY idx_customer_name (name)
);

CREATE TABLE supplier (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(128) NOT NULL,
  phone varchar(32) DEFAULT NULL,
  email varchar(128) DEFAULT NULL,
  address varchar(255) DEFAULT NULL,
  status tinyint NOT NULL DEFAULT 1,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_supplier_name_deleted (name, deleted),
  KEY idx_supplier_phone (phone),
  KEY idx_supplier_name (name)
);

CREATE TABLE warehouse (
  id bigint NOT NULL AUTO_INCREMENT,
  code varchar(64) NOT NULL,
  name varchar(128) NOT NULL,
  address varchar(255) DEFAULT NULL,
  contact_name varchar(64) DEFAULT NULL,
  contact_phone varchar(32) DEFAULT NULL,
  status tinyint NOT NULL DEFAULT 1,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_warehouse_code_deleted (code, deleted),
  KEY idx_warehouse_name (name)
);

SET FOREIGN_KEY_CHECKS = 1;
