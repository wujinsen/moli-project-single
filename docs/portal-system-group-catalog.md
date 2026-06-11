# 门户系统分组目录（分类 + 常见系统）

> 选系统页按 `sys_system.system_group` 展示。  
> 前端改造：`meiling-ui/docs/portal-system-group-ui.md`  
> 展示顺序：`governance` → `business` → `ai` → `tech` → `ops` → `data` → `office`（空组不显示）

---

## 1. 管理与治理 `governance`

**放什么：** 平台级管理后台、统一身份与权限、系统注册与审计入口。

| 状态 | 系统 |
|------|------|
| ✅ 已注册 | **moli-admin**（moli-admin 管理后台，INTERNAL，默认系统） |
| 常见 | 统一权限中心、IAM、租户管理、系统注册中心、配置中心、审计门户 |

**不归此类：** 具体业务系统（CRM）、纯监控运维台 → 见 `business` / `ops`。

---

## 2. 业务应用 `business`

**放什么：** 面向业务人员的条线系统、交易与运营类应用（**电商、会员、客服、用户增长** 均在此类）。

| 状态 | 系统 |
|------|------|
| ✅ 已注册 | **crm-demo** CRM 客户管理 |
| ✅ 已注册 | **ecom-mall** 电商商城 |
| ✅ 已注册 | **ecom-order** 订单中心 |
| ✅ 已注册 | **ecom-product** 商品中心 |
| ✅ 已注册 | **ecom-inventory** 库存中心 |
| ✅ 已注册 | **ecom-payment** 支付中心 |
| ✅ 已注册 | **member-center** 会员中心 |
| ✅ 已注册 | **points-center** 积分中心 |
| ✅ 已注册 | **cs-ticket** 客服系统 |
| ✅ 已注册 | **user-growth** 用户增长平台 |

**用户增长归 `business`：** 拉新、留存、活动、投放、A/B 等 **运营业务系统**；若仅是增长分析看板 → `data`。

**不归此类：** 纯 BI 报表 → `data`；大模型助手 → `ai`。

---

## 3. AI 应用 `ai`

**放什么：** 大模型、Copilot、智能体、AIGC 等 **以 AI 能力为主入口** 的产品。

| 状态 | 系统 |
|------|------|
| ✅ 已注册 | **ai-copilot**（AI 智能助手，EXTERNAL，演示） |
| 常见 | ChatGPT / 企业版 GPT 门户、内部 Copilot、智能客服机器人、代码助手、AIGC 创作台、Agent 编排平台 |

**不归此类：** 传统 BI 报表 → `data`；API 网关、MLOps 训练平台（偏研发）→ `tech`。

---

## 4. 技术类平台 `tech`

**放什么：** 研发与集成用的技术中台、工程效能类平台。

| 状态 | 系统 |
|------|------|
| ✅ 已注册 | **api-portal** API 开放平台 |
| ✅ 已注册 | **api-gateway** API 网关 |
| ✅ 已注册 | **ms-governance** 微服务治理 |
| ✅ 已注册 | **svc-registry** 注册配置中心 |
| ✅ 已注册 | **lowcode-studio** 低代码平台 |
| ✅ 已注册 | **devops-cicd** CI/CD 流水线 |
| ✅ 已注册 | **artifact-repo** 制品库 |
| ✅ 已注册 | **code-repo** 代码仓库 |

**不归此类：** 监控告警、日志检索、发布运维台 → `ops`；SQL/指标开发 → `data`。

---

## 5. 运维与保障 `ops`

**放什么：** 保障系统稳定运行的监控、发布、资源与故障处理。

| 状态 | 系统 |
|------|------|
| ✅ 已注册 | **moli-ops** 运维总控台 |
| ✅ 已注册 | **monitor-alert** 监控告警中心 |
| ✅ 已注册 | **log-platform** 日志检索平台 |
| ✅ 已注册 | **release-platform** 发布部署平台 |
| ✅ 已注册 | **cmdb-platform** CMDB 配置管理 |
| ✅ 已注册 | **trace-platform** 链路追踪平台 |
| ✅ 已注册 | **k8s-console** 容器云管控 |

**不归此类：** Flink/SQL 数据开发台 → `data`；微服务治理/API 网关 → `tech`。

---

## 6. 数据平台 `data`

**放什么：** 看数、用数、管数；含 BI、数仓、指标与 **大数据开发分析** 类产品。

| 状态 | 系统 |
|------|------|
| ✅ 已注册 | **bi-report** BI 报表中心 |
| ✅ 已注册 | **metric-platform** 指标平台 |
| ✅ 已注册 | **data-map** 数据地图 |
| ✅ 已注册 | **user-cdp** 用户画像 CDP |
| ✅ 已注册 | **data-warehouse** 离线数仓 |
| ✅ 已注册 | **data-dev** 数据开发平台 |
| ✅ 已注册 | **flink-studio** Flink 实时开发 |
| ✅ 已注册 | **data-quality** 数据质量 |
| ✅ 已注册 | **data-governance** 数据治理 |
| ✅ 已注册 | **realtime-lake** 实时湖仓 |

**不归此类：** 大模型对话 → `ai`；Hadoop/Yarn 集群运维 → `ops`；增长活动运营 → `business`。

---

## 7. 办公协同 `office`

**放什么：** 日常办公协作，与具体业务条线弱相关。

| 状态 | 系统 |
|------|------|
| ✅ 已注册 | **oa-office**（OA 办公协同，EXTERNAL，演示） |
| 常见 | OA 审批、企业邮件、日历/会议室、钉钉/飞书类门户、文档协作、HR 自助（请假考勤） |

**不归此类：** 业务 CRM、管理后台 → 各归其类。

---

## 当前演示数据（执行 `sql/seed_sys_system_portal_demo.sql` 后）

| 分组 | 数量 | 系统 |
|------|------|------|
| 管理与治理 | 1 | moli-admin |
| **业务应用** | **10** | crm-demo、ecom-mall、ecom-order、ecom-product、ecom-inventory、ecom-payment、member-center、points-center、cs-ticket、user-growth |
| AI 应用 | 1 | ai-copilot |
| **技术类平台** | **8** | api-portal、api-gateway、ms-governance、svc-registry、lowcode-studio、devops-cicd、artifact-repo、code-repo |
| **运维与保障** | **7** | moli-ops、monitor-alert、log-platform、release-platform、cmdb-platform、trace-platform、k8s-console |
| **数据平台** | **10** | bi-report、metric-platform、data-map、user-cdp、data-warehouse、data-dev、flink-studio、data-quality、data-governance、realtime-lake |
| 办公协同 | 1 | oa-office |

共 **38** 个系统；superadmin 选系统页 **7 个分组标题** 下共 **38 张卡片**（除 moli-admin 外均为外链演示）。
