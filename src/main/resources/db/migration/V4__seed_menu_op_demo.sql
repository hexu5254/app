-- 演示数据：与前端路由 meta.menuId=10001（工作台）对齐；Flyway 仅执行一次。
-- 角色 code：normal_user（普通用户，部分操作码；注册默认角色）、admin（管理员，菜单 10001 全量）。

INSERT INTO app_menu (id, parent_id, name, path, icon, menu_type, client_type, sequ, status, control_type)
VALUES
    (10001, NULL, '工作台', '/workbench', NULL, '2', '1', 10, '1', NULL),
    (10002, NULL, '示例列表页', '/demo/list', NULL, '2', '1', 20, '1', NULL)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('app_menu', 'id'), (SELECT COALESCE(MAX(id), 1) FROM app_menu));

INSERT INTO app_op_security (menu_id, code, name, group_id, sequ, status)
VALUES
    (10001, 'ADD', '新增', NULL, 1, '1'),
    (10001, 'EDIT', '编辑', NULL, 2, '1'),
    (10001, 'DELETE', '删除', NULL, 3, '1'),
    (10001, 'EXPORT', '导出', NULL, 4, '1'),
    (10001, 'VIEW', '查看', NULL, 5, '1'),
    (10002, 'ADD', '新增', NULL, 1, '1'),
    (10002, 'VIEW', '查看', NULL, 2, '1')
ON CONFLICT (menu_id, code) DO NOTHING;

SELECT setval(pg_get_serial_sequence('app_op_security', 'id'), (SELECT COALESCE(MAX(id), 1) FROM app_op_security));

INSERT INTO app_role (code, name, role_desc, sequ, is_inner, is_view_all, status,
                      validated_start_date, validated_end_date)
VALUES
    ('normal_user', '普通用户', '工作台：ADD+VIEW+EDIT；无 DELETE/EXPORT', 100, '0', '0', '1', NULL, NULL),
    ('admin', '管理员', '工作台全部操作码（应用角色，与 user_type=9 平台管理员不同）', 101, '0', '0', '1', NULL, NULL)
ON CONFLICT (code) DO NOTHING;

SELECT setval(pg_get_serial_sequence('app_role', 'id'), (SELECT COALESCE(MAX(id), 1) FROM app_role));

INSERT INTO app_op_assign (role_id, op_id)
SELECT r.id, o.id
FROM app_role r
JOIN app_op_security o ON o.menu_id = 10001 AND o.group_id IS NULL AND o.status = '1'
    AND o.code IN ('ADD', 'EDIT', 'VIEW')
WHERE r.code = 'normal_user'
ON CONFLICT (role_id, op_id) DO NOTHING;

INSERT INTO app_op_assign (role_id, op_id)
SELECT r.id, o.id
FROM app_role r
JOIN app_op_security o ON o.menu_id = 10001 AND o.group_id IS NULL AND o.status = '1'
WHERE r.code = 'admin'
ON CONFLICT (role_id, op_id) DO NOTHING;

INSERT INTO app_op_assign (role_id, op_id)
SELECT r.id, o.id
FROM app_role r
JOIN app_op_security o ON o.menu_id = 10002 AND o.group_id IS NULL AND o.status = '1'
WHERE r.code = 'normal_user'
ON CONFLICT (role_id, op_id) DO NOTHING;

-- 若存在 sm_user.id = 1，挂上 normal_user（首账号开箱验证）
INSERT INTO sm_role_user (user_id, role_id)
SELECT u.id, r.id
FROM sm_user u
CROSS JOIN app_role r
WHERE u.id = 1 AND r.code = 'normal_user'
ON CONFLICT DO NOTHING;
