SET NAMES utf8mb4;

/*
  Demo seed extension: add more brands/categories/products/skus/stocks/customers/orders
  Safe-ish inserts: avoid conflicts by checking NOT EXISTS on unique keys (with deleted=0)
*/

/* brands */
INSERT INTO brand (name, description, status)
SELECT 'ASUS', '主板/显卡/外设', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='ASUS' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'MSI', '主板/显卡/外设', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='MSI' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'GIGABYTE', '主板/显卡', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='GIGABYTE' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'ASRock', '主板', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='ASRock' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'Corsair', '内存/电源/机箱', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='Corsair' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'Crucial', '内存/固态硬盘', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='Crucial' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'Western Digital', '存储', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='Western Digital' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'Seagate', '存储', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='Seagate' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'Seasonic', '电源', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='Seasonic' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'Cooler Master', '散热/机箱/电源', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='Cooler Master' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'Noctua', '散热', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='Noctua' AND deleted=0);

INSERT INTO brand (name, description, status)
SELECT 'AOC', '显示器', 1
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name='AOC' AND deleted=0);

/* categories (top-level) */
INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT NULL, '电源', 6, 1, '/电源'
WHERE NOT EXISTS (
  SELECT 1 FROM product_category WHERE parent_id IS NULL AND name='电源' AND deleted=0
);

INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT NULL, '机箱', 7, 1, '/机箱'
WHERE NOT EXISTS (
  SELECT 1 FROM product_category WHERE parent_id IS NULL AND name='机箱' AND deleted=0
);

INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT NULL, '散热', 8, 1, '/散热'
WHERE NOT EXISTS (
  SELECT 1 FROM product_category WHERE parent_id IS NULL AND name='散热' AND deleted=0
);

INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT NULL, '机械硬盘', 9, 1, '/机械硬盘'
WHERE NOT EXISTS (
  SELECT 1 FROM product_category WHERE parent_id IS NULL AND name='机械硬盘' AND deleted=0
);

INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT NULL, '显示器', 10, 1, '/显示器'
WHERE NOT EXISTS (
  SELECT 1 FROM product_category WHERE parent_id IS NULL AND name='显示器' AND deleted=0
);

/* categories (second-level) */
INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT p.id, 'Intel 平台', 1, 2, CONCAT(p.path, '/Intel 平台')
FROM product_category p
WHERE p.parent_id IS NULL AND p.name='CPU' AND p.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM product_category c
    WHERE c.parent_id=p.id AND c.name='Intel 平台' AND c.deleted=0
  );

INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT p.id, 'AMD 平台', 2, 2, CONCAT(p.path, '/AMD 平台')
FROM product_category p
WHERE p.parent_id IS NULL AND p.name='CPU' AND p.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM product_category c
    WHERE c.parent_id=p.id AND c.name='AMD 平台' AND c.deleted=0
  );

INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT p.id, 'NVIDIA 显卡', 1, 2, CONCAT(p.path, '/NVIDIA 显卡')
FROM product_category p
WHERE p.parent_id IS NULL AND p.name='GPU' AND p.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM product_category c
    WHERE c.parent_id=p.id AND c.name='NVIDIA 显卡' AND c.deleted=0
  );

INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT p.id, 'AMD 显卡', 2, 2, CONCAT(p.path, '/AMD 显卡')
FROM product_category p
WHERE p.parent_id IS NULL AND p.name='GPU' AND p.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM product_category c
    WHERE c.parent_id=p.id AND c.name='AMD 显卡' AND c.deleted=0
  );

INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT p.id, 'DDR4', 1, 2, CONCAT(p.path, '/DDR4')
FROM product_category p
WHERE p.parent_id IS NULL AND p.name='内存' AND p.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM product_category c
    WHERE c.parent_id=p.id AND c.name='DDR4' AND c.deleted=0
  );

INSERT INTO product_category (parent_id, name, sort_order, level, path)
SELECT p.id, 'DDR5', 2, 2, CONCAT(p.path, '/DDR5')
FROM product_category p
WHERE p.parent_id IS NULL AND p.name='内存' AND p.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM product_category c
    WHERE c.parent_id=p.id AND c.name='DDR5' AND c.deleted=0
  );

/* suppliers/customers/warehouses */
INSERT INTO supplier (name, phone, email, address, status)
SELECT '北方总代', '010-00000003', 'north@supplier.example', '北京', 1
WHERE NOT EXISTS (SELECT 1 FROM supplier WHERE name='北方总代' AND deleted=0);

INSERT INTO supplier (name, phone, email, address, status)
SELECT '西南总代', '028-00000004', 'sw@supplier.example', '成都', 1
WHERE NOT EXISTS (SELECT 1 FROM supplier WHERE name='西南总代' AND deleted=0);

INSERT INTO customer (name, phone, email, address, status)
SELECT '王五', '13600000003', 'wangwu@example.com', '上海', 1
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE name='王五' AND deleted=0);

INSERT INTO customer (name, phone, email, address, status)
SELECT '赵六', '13700000004', 'zhaoliu@example.com', '深圳', 1
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE name='赵六' AND deleted=0);

INSERT INTO customer (name, phone, email, address, status)
SELECT '小明', '13500000005', 'xiaoming@example.com', '杭州', 1
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE name='小明' AND deleted=0);

INSERT INTO warehouse (code, name, address, contact_name, contact_phone, status)
SELECT 'WH002', '备用仓库', '上海', '李仓管', '021-00000006', 1
WHERE NOT EXISTS (SELECT 1 FROM warehouse WHERE code='WH002' AND deleted=0);

/* products */
INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_CPU_I7_13700K', 'Intel Core i7-13700K', c.id, b.id, '16核24线程，13代酷睿，适合游戏与生产力', 1
FROM product_category c
JOIN brand b ON b.name='Intel' AND b.deleted=0
WHERE c.name='CPU' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_CPU_I7_13700K' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_CPU_I9_13900K', 'Intel Core i9-13900K', c.id, b.id, '24核32线程，旗舰级性能', 1
FROM product_category c
JOIN brand b ON b.name='Intel' AND b.deleted=0
WHERE c.name='CPU' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_CPU_I9_13900K' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_CPU_R7_7800X3D', 'AMD Ryzen 7 7800X3D', c.id, b.id, '8核16线程，3D V-Cache 游戏神U', 1
FROM product_category c
JOIN brand b ON b.name='AMD' AND b.deleted=0
WHERE c.name='CPU' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_CPU_R7_7800X3D' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_GPU_RTX4070', 'NVIDIA GeForce RTX 4070', c.id, b.id, '12GB 显存，支持 DLSS 3', 1
FROM product_category c
JOIN brand b ON b.name='NVIDIA' AND b.deleted=0
WHERE c.name='GPU' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_GPU_RTX4070' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_GPU_RX7800XT', 'AMD Radeon RX 7800 XT', c.id, b.id, '16GB 显存，高性价比 2K/4K 游戏', 1
FROM product_category c
JOIN brand b ON b.name='AMD' AND b.deleted=0
WHERE c.name='GPU' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_GPU_RX7800XT' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_RAM_DDR5_32G_6000', 'Corsair DDR5 32GB 6000', c.id, b.id, '双通道 16GBx2，6000MHz', 1
FROM product_category c
JOIN brand b ON b.name='Corsair' AND b.deleted=0
WHERE c.name='内存' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_RAM_DDR5_32G_6000' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_RAM_DDR4_32G_3600', 'Crucial DDR4 32GB 3600', c.id, b.id, '双通道 16GBx2，3600MHz', 1
FROM product_category c
JOIN brand b ON b.name='Crucial' AND b.deleted=0
WHERE c.name='内存' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_RAM_DDR4_32G_3600' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_SSD_990PRO_2T', 'Samsung 990 PRO 2TB NVMe SSD', c.id, b.id, 'PCIe 4.0，旗舰 SSD，速度更快更稳', 1
FROM product_category c
JOIN brand b ON b.name='Samsung' AND b.deleted=0
WHERE c.name='固态硬盘' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_SSD_990PRO_2T' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_SSD_SN850X_2T', 'WD Black SN850X 2TB NVMe SSD', c.id, b.id, 'PCIe 4.0，高性能游戏 SSD', 1
FROM product_category c
JOIN brand b ON b.name='Western Digital' AND b.deleted=0
WHERE c.name='固态硬盘' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_SSD_SN850X_2T' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_MB_B760M_ASUS', 'ASUS B760M 主板', c.id, b.id, '支持 Intel 12/13 代，mATX', 1
FROM product_category c
JOIN brand b ON b.name='ASUS' AND b.deleted=0
WHERE c.name='主板' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_MB_B760M_ASUS' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_MB_B650_MSI', 'MSI B650 主板', c.id, b.id, '支持 AM5 平台，DDR5', 1
FROM product_category c
JOIN brand b ON b.name='MSI' AND b.deleted=0
WHERE c.name='主板' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_MB_B650_MSI' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_PSU_RM750X', 'Corsair RM750x 750W 电源', c.id, b.id, '80Plus Gold，全模组', 1
FROM product_category c
JOIN brand b ON b.name='Corsair' AND b.deleted=0
WHERE c.name='电源' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_PSU_RM750X' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_PSU_FOCUS_GX850', 'Seasonic Focus GX-850 850W 电源', c.id, b.id, '80Plus Gold，高稳定', 1
FROM product_category c
JOIN brand b ON b.name='Seasonic' AND b.deleted=0
WHERE c.name='电源' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_PSU_FOCUS_GX850' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_COOL_NHD15', 'Noctua NH-D15 风冷散热器', c.id, b.id, '旗舰双塔风冷，兼容多平台', 1
FROM product_category c
JOIN brand b ON b.name='Noctua' AND b.deleted=0
WHERE c.name='散热' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_COOL_NHD15' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_COOL_ML240', 'Cooler Master ML240 水冷散热器', c.id, b.id, '240mm 一体水冷', 1
FROM product_category c
JOIN brand b ON b.name='Cooler Master' AND b.deleted=0
WHERE c.name='散热' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_COOL_ML240' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_HDD_BARRACUDA_4T', 'Seagate BarraCuda 4TB HDD', c.id, b.id, '7200RPM 大容量机械硬盘', 1
FROM product_category c
JOIN brand b ON b.name='Seagate' AND b.deleted=0
WHERE c.name='机械硬盘' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_HDD_BARRACUDA_4T' AND p.deleted=0);

INSERT INTO product (spu_code, name, category_id, brand_id, description, status)
SELECT 'SPU_MONITOR_AOC_27_2K_165', 'AOC 27英寸 2K 165Hz 显示器', c.id, b.id, '2K 高刷电竞显示器', 1
FROM product_category c
JOIN brand b ON b.name='AOC' AND b.deleted=0
WHERE c.name='显示器' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product p WHERE p.spu_code='SPU_MONITOR_AOC_27_2K_165' AND p.deleted=0);

/* SKUs */
INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_CPU_I7_13700K_BOX', '690000001001', 'i7-13700K 盒装', JSON_OBJECT('package','boxed'), 2799.00, 2500.00, 1
FROM product p
WHERE p.spu_code='SPU_CPU_I7_13700K' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_CPU_I7_13700K_BOX' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_CPU_I9_13900K_BOX', '690000001002', 'i9-13900K 盒装', JSON_OBJECT('package','boxed'), 4599.00, 4200.00, 1
FROM product p
WHERE p.spu_code='SPU_CPU_I9_13900K' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_CPU_I9_13900K_BOX' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_CPU_R7_7800X3D_BOX', '690000001003', 'Ryzen 7 7800X3D 盒装', JSON_OBJECT('package','boxed'), 3299.00, 3000.00, 1
FROM product p
WHERE p.spu_code='SPU_CPU_R7_7800X3D' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_CPU_R7_7800X3D_BOX' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_GPU_RTX4070_12G', '690000001101', 'RTX 4070 12GB', JSON_OBJECT('vram','12GB'), 4199.00, 3800.00, 1
FROM product p
WHERE p.spu_code='SPU_GPU_RTX4070' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_GPU_RTX4070_12G' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_GPU_RX7800XT_16G', '690000001102', 'RX 7800 XT 16GB', JSON_OBJECT('vram','16GB'), 3999.00, 3600.00, 1
FROM product p
WHERE p.spu_code='SPU_GPU_RX7800XT' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_GPU_RX7800XT_16G' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_RAM_DDR5_32G_6000', '690000001201', 'DDR5 32GB 6000 (16x2)', JSON_OBJECT('capacity','32GB','speed','6000','type','DDR5'), 799.00, 650.00, 1
FROM product p
WHERE p.spu_code='SPU_RAM_DDR5_32G_6000' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_RAM_DDR5_32G_6000' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_RAM_DDR4_32G_3600', '690000001202', 'DDR4 32GB 3600 (16x2)', JSON_OBJECT('capacity','32GB','speed','3600','type','DDR4'), 499.00, 380.00, 1
FROM product p
WHERE p.spu_code='SPU_RAM_DDR4_32G_3600' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_RAM_DDR4_32G_3600' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_SSD_990PRO_2T', '690000001301', 'Samsung 990 PRO 2TB', JSON_OBJECT('capacity','2TB','interface','NVMe','pcie','4.0'), 1199.00, 980.00, 1
FROM product p
WHERE p.spu_code='SPU_SSD_990PRO_2T' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_SSD_990PRO_2T' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_SSD_SN850X_2T', '690000001302', 'WD SN850X 2TB', JSON_OBJECT('capacity','2TB','interface','NVMe','pcie','4.0'), 1099.00, 900.00, 1
FROM product p
WHERE p.spu_code='SPU_SSD_SN850X_2T' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_SSD_SN850X_2T' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_MB_B760M_ASUS', '690000001401', 'ASUS B760M', JSON_OBJECT('chipset','B760','form','mATX','socket','LGA1700'), 899.00, 760.00, 1
FROM product p
WHERE p.spu_code='SPU_MB_B760M_ASUS' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_MB_B760M_ASUS' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_MB_B650_MSI', '690000001402', 'MSI B650', JSON_OBJECT('chipset','B650','socket','AM5','memory','DDR5'), 1199.00, 980.00, 1
FROM product p
WHERE p.spu_code='SPU_MB_B650_MSI' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_MB_B650_MSI' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_PSU_RM750X', '690000001501', 'RM750x 750W', JSON_OBJECT('watt','750W','rating','Gold','modular','full'), 799.00, 650.00, 1
FROM product p
WHERE p.spu_code='SPU_PSU_RM750X' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_PSU_RM750X' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_PSU_FOCUS_GX850', '690000001502', 'Focus GX-850 850W', JSON_OBJECT('watt','850W','rating','Gold','modular','full'), 999.00, 820.00, 1
FROM product p
WHERE p.spu_code='SPU_PSU_FOCUS_GX850' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_PSU_FOCUS_GX850' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_COOL_NHD15', '690000001601', 'NH-D15 风冷', JSON_OBJECT('type','air','tdp','250W'), 699.00, 520.00, 1
FROM product p
WHERE p.spu_code='SPU_COOL_NHD15' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_COOL_NHD15' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_COOL_ML240', '690000001602', 'ML240 水冷', JSON_OBJECT('type','aio','size','240mm'), 499.00, 360.00, 1
FROM product p
WHERE p.spu_code='SPU_COOL_ML240' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_COOL_ML240' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_HDD_BARRACUDA_4T', '690000001701', 'BarraCuda 4TB', JSON_OBJECT('capacity','4TB','rpm','7200'), 499.00, 400.00, 1
FROM product p
WHERE p.spu_code='SPU_HDD_BARRACUDA_4T' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_HDD_BARRACUDA_4T' AND s.deleted=0);

INSERT INTO product_sku (product_id, sku_code, barcode, name, spec_json, price, cost, status)
SELECT p.id, 'SKU_MONITOR_AOC_27_2K_165', '690000001801', 'AOC 27" 2K 165Hz', JSON_OBJECT('size','27','resolution','2560x1440','refresh','165Hz'), 999.00, 820.00, 1
FROM product p
WHERE p.spu_code='SPU_MONITOR_AOC_27_2K_165' AND p.deleted=0
  AND NOT EXISTS (SELECT 1 FROM product_sku s WHERE s.sku_code='SKU_MONITOR_AOC_27_2K_165' AND s.deleted=0);

/* inventory stock: WH001 + WH002 */
INSERT INTO inventory_stock (warehouse_id, sku_id, quantity, version)
SELECT w.id, s.id, 30, 0
FROM warehouse w
JOIN product_sku s ON s.deleted=0
WHERE w.deleted=0
  AND w.code IN ('WH001', 'WH002')
  AND s.sku_code IN (
    'SKU_CPU_I7_13700K_BOX',
    'SKU_CPU_I9_13900K_BOX',
    'SKU_CPU_R7_7800X3D_BOX',
    'SKU_GPU_RTX4070_12G',
    'SKU_GPU_RX7800XT_16G',
    'SKU_RAM_DDR5_32G_6000',
    'SKU_RAM_DDR4_32G_3600',
    'SKU_SSD_990PRO_2T',
    'SKU_SSD_SN850X_2T',
    'SKU_MB_B760M_ASUS',
    'SKU_MB_B650_MSI',
    'SKU_PSU_RM750X',
    'SKU_PSU_FOCUS_GX850',
    'SKU_COOL_NHD15',
    'SKU_COOL_ML240',
    'SKU_HDD_BARRACUDA_4T',
    'SKU_MONITOR_AOC_27_2K_165'
  )
  AND NOT EXISTS (
    SELECT 1 FROM inventory_stock x
    WHERE x.warehouse_id=w.id AND x.sku_id=s.id AND x.deleted=0
  );

/* sales orders (a few demo orders) */
INSERT INTO sales_order (order_no, customer_id, status, total_amount, paid_amount, remark, ordered_at)
SELECT 'SO202512180001', c.id, 1, 2799.00, 2799.00, 'demo: i7 盒装', (NOW() - INTERVAL '5' DAY)
FROM customer c
WHERE c.name='张三' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM sales_order o WHERE o.order_no='SO202512180001' AND o.deleted=0);

INSERT INTO sales_order_item (sales_order_id, sku_id, price, quantity, amount, shipped_quantity)
SELECT o.id, s.id, 2799.00, 1, 2799.00, 1
FROM sales_order o
JOIN product_sku s ON s.sku_code='SKU_CPU_I7_13700K_BOX' AND s.deleted=0
WHERE o.order_no='SO202512180001' AND o.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM sales_order_item i
    WHERE i.sales_order_id=o.id AND i.sku_id=s.id AND i.deleted=0
  );

INSERT INTO payment_record (pay_no, sales_order_id, amount, pay_method, paid_at, remark)
SELECT 'PAY202512180001', o.id, 2799.00, 'wechat', (NOW() - INTERVAL '5' DAY), 'demo pay'
FROM sales_order o
WHERE o.order_no='SO202512180001' AND o.deleted=0
  AND NOT EXISTS (SELECT 1 FROM payment_record p WHERE p.pay_no='PAY202512180001' AND p.deleted=0);

INSERT INTO sales_order (order_no, customer_id, status, total_amount, paid_amount, remark, ordered_at)
SELECT 'SO202512180002', c.id, 0, 4199.00, 0.00, 'demo: RTX 4070', (NOW() - INTERVAL '2' DAY)
FROM customer c
WHERE c.name='李四' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM sales_order o WHERE o.order_no='SO202512180002' AND o.deleted=0);

INSERT INTO sales_order_item (sales_order_id, sku_id, price, quantity, amount, shipped_quantity)
SELECT o.id, s.id, 4199.00, 1, 4199.00, 0
FROM sales_order o
JOIN product_sku s ON s.sku_code='SKU_GPU_RTX4070_12G' AND s.deleted=0
WHERE o.order_no='SO202512180002' AND o.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM sales_order_item i
    WHERE i.sales_order_id=o.id AND i.sku_id=s.id AND i.deleted=0
  );

INSERT INTO sales_order (order_no, customer_id, status, total_amount, paid_amount, remark, ordered_at)
SELECT 'SO202512180003', c.id, 2, 2198.00, 2198.00, 'demo: SSD+内存', (NOW() - INTERVAL '10' DAY)
FROM customer c
WHERE c.name='王五' AND c.deleted=0
  AND NOT EXISTS (SELECT 1 FROM sales_order o WHERE o.order_no='SO202512180003' AND o.deleted=0);

INSERT INTO sales_order_item (sales_order_id, sku_id, price, quantity, amount, shipped_quantity)
SELECT o.id, s.id, 1099.00, 1, 1099.00, 1
FROM sales_order o
JOIN product_sku s ON s.sku_code='SKU_SSD_SN850X_2T' AND s.deleted=0
WHERE o.order_no='SO202512180003' AND o.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM sales_order_item i
    WHERE i.sales_order_id=o.id AND i.sku_id=s.id AND i.deleted=0
  );

INSERT INTO sales_order_item (sales_order_id, sku_id, price, quantity, amount, shipped_quantity)
SELECT o.id, s.id, 499.00, 1, 499.00, 1
FROM sales_order o
JOIN product_sku s ON s.sku_code='SKU_RAM_DDR4_32G_3600' AND s.deleted=0
WHERE o.order_no='SO202512180003' AND o.deleted=0
  AND NOT EXISTS (
    SELECT 1 FROM sales_order_item i
    WHERE i.sales_order_id=o.id AND i.sku_id=s.id AND i.deleted=0
  );

INSERT INTO payment_record (pay_no, sales_order_id, amount, pay_method, paid_at, remark)
SELECT 'PAY202512180003', o.id, 2198.00, 'alipay', (NOW() - INTERVAL '10' DAY), 'demo pay'
FROM sales_order o
WHERE o.order_no='SO202512180003' AND o.deleted=0
  AND NOT EXISTS (SELECT 1 FROM payment_record p WHERE p.pay_no='PAY202512180003' AND p.deleted=0);
