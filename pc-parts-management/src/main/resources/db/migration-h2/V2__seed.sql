INSERT INTO sys_role (code, name, description) VALUES
('ADMIN', '管理员', '系统管理员'),
('SALES', '销售', '销售人员'),
('WAREHOUSE', '仓库', '仓库管理员'),
('FINANCE', '财务', '财务人员');

INSERT INTO sys_user (username, password, nickname, status) VALUES
('admin', '{noop}123456', '管理员', 1);

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
