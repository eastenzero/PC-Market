CREATE DATABASE IF NOT EXISTS pc_parts DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE pc_parts;

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
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_username_deleted (username, deleted),
  KEY idx_sys_user_phone (phone),
  KEY idx_sys_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE sys_role (
  id bigint NOT NULL AUTO_INCREMENT,
  code varchar(64) NOT NULL,
  name varchar(64) NOT NULL,
  description varchar(255) DEFAULT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_code_deleted (code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_permission_code_deleted (code, deleted),
  KEY idx_sys_permission_parent_id (parent_id),
  CONSTRAINT fk_sys_permission_parent_id FOREIGN KEY (parent_id) REFERENCES sys_permission (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE sys_user_role (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_role_user_id_role_id_deleted (user_id, role_id, deleted),
  KEY idx_sys_user_role_role_id (role_id),
  CONSTRAINT fk_sys_user_role_user_id FOREIGN KEY (user_id) REFERENCES sys_user (id),
  CONSTRAINT fk_sys_user_role_role_id FOREIGN KEY (role_id) REFERENCES sys_role (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE sys_role_permission (
  id bigint NOT NULL AUTO_INCREMENT,
  role_id bigint NOT NULL,
  permission_id bigint NOT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_permission_role_id_permission_id_deleted (role_id, permission_id, deleted),
  KEY idx_sys_role_permission_permission_id (permission_id),
  CONSTRAINT fk_sys_role_permission_role_id FOREIGN KEY (role_id) REFERENCES sys_role (id),
  CONSTRAINT fk_sys_role_permission_permission_id FOREIGN KEY (permission_id) REFERENCES sys_permission (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE brand (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(64) NOT NULL,
  description varchar(255) DEFAULT NULL,
  status tinyint NOT NULL DEFAULT 1,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_brand_name_deleted (name, deleted),
  KEY idx_brand_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_product_category_parent_id_name_deleted (parent_id, name, deleted),
  KEY idx_product_category_parent_id (parent_id),
  CONSTRAINT fk_product_category_parent_id FOREIGN KEY (parent_id) REFERENCES product_category (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_product_spu_code_deleted (spu_code, deleted),
  KEY idx_product_name (name),
  KEY idx_product_category_id (category_id),
  KEY idx_product_brand_id (brand_id),
  CONSTRAINT fk_product_category_id FOREIGN KEY (category_id) REFERENCES product_category (id),
  CONSTRAINT fk_product_brand_id FOREIGN KEY (brand_id) REFERENCES brand (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_product_sku_sku_code_deleted (sku_code, deleted),
  KEY idx_product_sku_product_id (product_id),
  KEY idx_product_sku_barcode (barcode),
  KEY idx_product_sku_name (name),
  CONSTRAINT fk_product_sku_product_id FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_customer_name_deleted (name, deleted),
  KEY idx_customer_phone (phone),
  KEY idx_customer_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_supplier_name_deleted (name, deleted),
  KEY idx_supplier_phone (phone),
  KEY idx_supplier_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_warehouse_code_deleted (code, deleted),
  KEY idx_warehouse_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE inventory_stock (
  id bigint NOT NULL AUTO_INCREMENT,
  warehouse_id bigint NOT NULL,
  sku_id bigint NOT NULL,
  quantity int NOT NULL DEFAULT 0,
  version int NOT NULL DEFAULT 0,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_inventory_stock_warehouse_id_sku_id_deleted (warehouse_id, sku_id, deleted),
  KEY idx_inventory_stock_sku_id (sku_id),
  CONSTRAINT fk_inventory_stock_warehouse_id FOREIGN KEY (warehouse_id) REFERENCES warehouse (id),
  CONSTRAINT fk_inventory_stock_sku_id FOREIGN KEY (sku_id) REFERENCES product_sku (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE inventory_movement (
  id bigint NOT NULL AUTO_INCREMENT,
  warehouse_id bigint NOT NULL,
  sku_id bigint NOT NULL,
  movement_type tinyint NOT NULL,
  biz_type varchar(32) NOT NULL,
  biz_id bigint DEFAULT NULL,
  biz_no varchar(64) DEFAULT NULL,
  quantity int NOT NULL,
  before_quantity int NOT NULL,
  after_quantity int NOT NULL,
  remark varchar(255) DEFAULT NULL,
  moved_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_inventory_movement_wh_sku_moved_at (warehouse_id, sku_id, moved_at),
  KEY idx_inventory_movement_biz (biz_type, biz_id),
  CONSTRAINT fk_inventory_movement_warehouse_id FOREIGN KEY (warehouse_id) REFERENCES warehouse (id),
  CONSTRAINT fk_inventory_movement_sku_id FOREIGN KEY (sku_id) REFERENCES product_sku (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE purchase_order (
  id bigint NOT NULL AUTO_INCREMENT,
  order_no varchar(64) NOT NULL,
  supplier_id bigint NOT NULL,
  status tinyint NOT NULL DEFAULT 0,
  total_amount decimal(12,2) NOT NULL DEFAULT 0.00,
  remark varchar(255) DEFAULT NULL,
  ordered_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_purchase_order_order_no_deleted (order_no, deleted),
  KEY idx_purchase_order_supplier_id (supplier_id),
  KEY idx_purchase_order_status_ordered_at (status, ordered_at),
  CONSTRAINT fk_purchase_order_supplier_id FOREIGN KEY (supplier_id) REFERENCES supplier (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE purchase_order_item (
  id bigint NOT NULL AUTO_INCREMENT,
  purchase_order_id bigint NOT NULL,
  sku_id bigint NOT NULL,
  price decimal(12,2) NOT NULL DEFAULT 0.00,
  quantity int NOT NULL DEFAULT 0,
  amount decimal(12,2) NOT NULL DEFAULT 0.00,
  received_quantity int NOT NULL DEFAULT 0,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_purchase_order_item_order_id (purchase_order_id),
  KEY idx_purchase_order_item_sku_id (sku_id),
  CONSTRAINT fk_purchase_order_item_order_id FOREIGN KEY (purchase_order_id) REFERENCES purchase_order (id),
  CONSTRAINT fk_purchase_order_item_sku_id FOREIGN KEY (sku_id) REFERENCES product_sku (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE stock_in (
  id bigint NOT NULL AUTO_INCREMENT,
  stock_in_no varchar(64) NOT NULL,
  purchase_order_id bigint DEFAULT NULL,
  warehouse_id bigint NOT NULL,
  status tinyint NOT NULL DEFAULT 0,
  total_amount decimal(12,2) NOT NULL DEFAULT 0.00,
  received_at datetime DEFAULT NULL,
  remark varchar(255) DEFAULT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_stock_in_stock_in_no_deleted (stock_in_no, deleted),
  KEY idx_stock_in_purchase_order_id (purchase_order_id),
  KEY idx_stock_in_warehouse_id (warehouse_id),
  KEY idx_stock_in_status_received_at (status, received_at),
  CONSTRAINT fk_stock_in_purchase_order_id FOREIGN KEY (purchase_order_id) REFERENCES purchase_order (id),
  CONSTRAINT fk_stock_in_warehouse_id FOREIGN KEY (warehouse_id) REFERENCES warehouse (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE stock_in_item (
  id bigint NOT NULL AUTO_INCREMENT,
  stock_in_id bigint NOT NULL,
  sku_id bigint NOT NULL,
  price decimal(12,2) NOT NULL DEFAULT 0.00,
  quantity int NOT NULL DEFAULT 0,
  amount decimal(12,2) NOT NULL DEFAULT 0.00,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_stock_in_item_stock_in_id (stock_in_id),
  KEY idx_stock_in_item_sku_id (sku_id),
  CONSTRAINT fk_stock_in_item_stock_in_id FOREIGN KEY (stock_in_id) REFERENCES stock_in (id),
  CONSTRAINT fk_stock_in_item_sku_id FOREIGN KEY (sku_id) REFERENCES product_sku (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE sales_order (
  id bigint NOT NULL AUTO_INCREMENT,
  order_no varchar(64) NOT NULL,
  customer_id bigint NOT NULL,
  status tinyint NOT NULL DEFAULT 0,
  total_amount decimal(12,2) NOT NULL DEFAULT 0.00,
  paid_amount decimal(12,2) NOT NULL DEFAULT 0.00,
  remark varchar(255) DEFAULT NULL,
  ordered_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sales_order_order_no_deleted (order_no, deleted),
  KEY idx_sales_order_customer_id (customer_id),
  KEY idx_sales_order_status_ordered_at (status, ordered_at),
  CONSTRAINT fk_sales_order_customer_id FOREIGN KEY (customer_id) REFERENCES customer (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE sales_order_item (
  id bigint NOT NULL AUTO_INCREMENT,
  sales_order_id bigint NOT NULL,
  sku_id bigint NOT NULL,
  price decimal(12,2) NOT NULL DEFAULT 0.00,
  quantity int NOT NULL DEFAULT 0,
  amount decimal(12,2) NOT NULL DEFAULT 0.00,
  shipped_quantity int NOT NULL DEFAULT 0,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_sales_order_item_order_id (sales_order_id),
  KEY idx_sales_order_item_sku_id (sku_id),
  CONSTRAINT fk_sales_order_item_order_id FOREIGN KEY (sales_order_id) REFERENCES sales_order (id),
  CONSTRAINT fk_sales_order_item_sku_id FOREIGN KEY (sku_id) REFERENCES product_sku (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE stock_out (
  id bigint NOT NULL AUTO_INCREMENT,
  stock_out_no varchar(64) NOT NULL,
  sales_order_id bigint DEFAULT NULL,
  warehouse_id bigint NOT NULL,
  status tinyint NOT NULL DEFAULT 0,
  total_amount decimal(12,2) NOT NULL DEFAULT 0.00,
  shipped_at datetime DEFAULT NULL,
  remark varchar(255) DEFAULT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_stock_out_stock_out_no_deleted (stock_out_no, deleted),
  KEY idx_stock_out_sales_order_id (sales_order_id),
  KEY idx_stock_out_warehouse_id (warehouse_id),
  KEY idx_stock_out_status_shipped_at (status, shipped_at),
  CONSTRAINT fk_stock_out_sales_order_id FOREIGN KEY (sales_order_id) REFERENCES sales_order (id),
  CONSTRAINT fk_stock_out_warehouse_id FOREIGN KEY (warehouse_id) REFERENCES warehouse (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE stock_out_item (
  id bigint NOT NULL AUTO_INCREMENT,
  stock_out_id bigint NOT NULL,
  sku_id bigint NOT NULL,
  price decimal(12,2) NOT NULL DEFAULT 0.00,
  quantity int NOT NULL DEFAULT 0,
  amount decimal(12,2) NOT NULL DEFAULT 0.00,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_stock_out_item_stock_out_id (stock_out_id),
  KEY idx_stock_out_item_sku_id (sku_id),
  CONSTRAINT fk_stock_out_item_stock_out_id FOREIGN KEY (stock_out_id) REFERENCES stock_out (id),
  CONSTRAINT fk_stock_out_item_sku_id FOREIGN KEY (sku_id) REFERENCES product_sku (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE payment_record (
  id bigint NOT NULL AUTO_INCREMENT,
  pay_no varchar(64) NOT NULL,
  sales_order_id bigint NOT NULL,
  amount decimal(12,2) NOT NULL DEFAULT 0.00,
  pay_method varchar(32) NOT NULL,
  paid_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remark varchar(255) DEFAULT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_payment_record_pay_no_deleted (pay_no, deleted),
  KEY idx_payment_record_sales_order_id (sales_order_id),
  KEY idx_payment_record_paid_at (paid_at),
  CONSTRAINT fk_payment_record_sales_order_id FOREIGN KEY (sales_order_id) REFERENCES sales_order (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE audit_log (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint DEFAULT NULL,
  module varchar(64) NOT NULL,
  action varchar(64) NOT NULL,
  entity_type varchar(64) DEFAULT NULL,
  entity_id bigint DEFAULT NULL,
  request_uri varchar(255) DEFAULT NULL,
  request_method varchar(16) DEFAULT NULL,
  ip varchar(64) DEFAULT NULL,
  user_agent varchar(255) DEFAULT NULL,
  detail_json json DEFAULT NULL,
  deleted tinyint(1) NOT NULL DEFAULT 0,
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by bigint DEFAULT NULL,
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_audit_log_user_id (user_id),
  KEY idx_audit_log_module_created_at (module, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
