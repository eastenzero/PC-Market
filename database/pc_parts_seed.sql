USE pc_parts;

INSERT INTO sys_role (code, name, description) VALUES
('ADMIN', '管理员', '系统管理员'),
('SALES', '销售', '销售人员'),
('WAREHOUSE', '仓库', '仓库管理员'),
('FINANCE', '财务', '财务人员');

INSERT INTO sys_user (username, password, nickname, status) VALUES
('admin', '123456', '管理员', 1);

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
JOIN sys_role r ON r.code = 'ADMIN'
WHERE u.username = 'admin' AND u.deleted = 0 AND r.deleted = 0;

INSERT INTO brand (name, description, status) VALUES
('Intel', 'CPU/芯片', 1),
('AMD', 'CPU/芯片', 1),
('NVIDIA', '显卡', 1),
('Samsung', '存储', 1),
('Kingston', '内存/存储', 1);

INSERT INTO product_category (parent_id, name, sort_order, level, path) VALUES
(NULL, 'CPU', 1, 1, '/CPU'),
(NULL, 'GPU', 2, 1, '/GPU'),
(NULL, '内存', 3, 1, '/内存'),
(NULL, '固态硬盘', 4, 1, '/固态硬盘'),
(NULL, '主板', 5, 1, '/主板');

INSERT INTO supplier (name, phone, address, status) VALUES
('华南总代', '0755-00000001', '深圳', 1),
('华东总代', '021-00000002', '上海', 1);

INSERT INTO customer (name, phone, address, status) VALUES
('张三', '13800000001', '广州', 1),
('李四', '13900000002', '北京', 1);

INSERT INTO warehouse (code, name, address, contact_name, contact_phone, status) VALUES
('WH001', '主仓库', '深圳', '王仓管', '0755-00000003', 1);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_CPU_I5_12400F', 'Intel Core i5-12400F', c.id, b.id, '6核12线程', 1
FROM product_category c
JOIN brand b ON b.name='Intel'
WHERE c.name='CPU' AND c.deleted=0 AND b.deleted=0;

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_CPU_R5_5600', 'AMD Ryzen 5 5600', c.id, b.id, '6核12线程', 1
FROM product_category c
JOIN brand b ON b.name='AMD'
WHERE c.name='CPU' AND c.deleted=0 AND b.deleted=0;

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_GPU_RTX4060', 'NVIDIA GeForce RTX 4060', c.id, b.id, '8GB 显存', 1
FROM product_category c
JOIN brand b ON b.name='NVIDIA'
WHERE c.name='GPU' AND c.deleted=0 AND b.deleted=0;

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_RAM_DDR4_16G', 'Kingston DDR4 16GB 3200', c.id, b.id, '台式机内存', 1
FROM product_category c
JOIN brand b ON b.name='Kingston'
WHERE c.name='内存' AND c.deleted=0 AND b.deleted=0;

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_SSD_1T', 'Samsung 980 1TB NVMe SSD', c.id, b.id, 'NVMe M.2', 1
FROM product_category c
JOIN brand b ON b.name='Samsung'
WHERE c.name='固态硬盘' AND c.deleted=0 AND b.deleted=0;

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_CPU_I5_12400F', '690000000001', 'i5-12400F 盒装', JSON_OBJECT('package','boxed'), 1399.00, 1200.00, 1
FROM product p WHERE p.spu_code='SPU_CPU_I5_12400F' AND p.deleted=0;

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_CPU_R5_5600', '690000000002', 'Ryzen 5 5600 盒装', JSON_OBJECT('package','boxed'), 999.00, 850.00, 1
FROM product p WHERE p.spu_code='SPU_CPU_R5_5600' AND p.deleted=0;

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_GPU_RTX4060_8G', '690000000003', 'RTX 4060 8GB', JSON_OBJECT('vram','8GB'), 2399.00, 2100.00, 1
FROM product p WHERE p.spu_code='SPU_GPU_RTX4060' AND p.deleted=0;

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_RAM_DDR4_16G_3200', '690000000004', 'DDR4 16GB 3200', JSON_OBJECT('capacity','16GB','speed','3200'), 299.00, 230.00, 1
FROM product p WHERE p.spu_code='SPU_RAM_DDR4_16G' AND p.deleted=0;

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_SSD_980_1T', '690000000005', 'Samsung 980 1TB', JSON_OBJECT('capacity','1TB','interface','NVMe'), 499.00, 420.00, 1
FROM product p WHERE p.spu_code='SPU_SSD_1T' AND p.deleted=0;

INSERT INTO inventory_stock (warehouse_id, sku_id, quantity, version)
SELECT w.id, s.id, 10, 0
FROM warehouse w
JOIN product_sku s
WHERE w.code='WH001' AND w.deleted=0 AND s.deleted=0;
