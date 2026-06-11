# 其他系统接入 moli-admin SSO

最后更新: 2026-06-10

> **moli-admin** 即本项目（Maven 目录 `moli-server`）。下文「moli-admin」均指本服务，不与 `moli-server` 区分。

## 1. 分工

| 能力 | moli-admin（本项目） | 其他系统（CRM 等） |
|------|----------------------|-------------------|
| 登录 | ✅ | ❌ |
| 分配用户能进哪些系统 | ✅ `insertUserSystem` | ❌ |
| 本系统角色 / 菜单 | ✅ 本库 RBAC | ✅ 各自库 |

`/sso/validate` 只返回身份，不返回菜单或角色。

## 2. 在 moli-admin 分配系统准入

```http
PUT /user/insertUserSystem
{ "userId": 10001, "systemIds": [1, 2] }
```

与 `insertUserRole`（本系统内角色）分开配置。

## 3. 其他系统接入步骤

1. 超管在 moli-admin 登记系统（`POST /system`，`ssoMode=EXTERNAL`）。
2. 用户从门户 `POST /system/enter` 拿到 `redirectUrl?ticket=...`。
3. 其他系统后端调 moli-admin：

```http
POST /sso/validate
X-Sso-Secret: <约定密钥>

{ "ticket": "...", "systemCode": "crm" }
```

4. 用返回的 `userName` 映射本地用户，加载本地 RBAC；若 `fullPermission=true`（`superadmin`/`admin`），外部系统应授予本地最大权限。

## 4. 配置

```yaml
# application.yml
sso:
  enabled: true
  ticket-ttl-seconds: 60
  shared-secret: ${SSO_SHARED_SECRET:}
```

## 5. 相关文档

- [多系统 SSO 设计](multi-system-sso-design.md)
