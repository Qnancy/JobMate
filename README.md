# JobMate

浙大就业信息类 Web 应用：职位与招聘活动浏览、搜索、收藏；管理员维护企业与岗位/活动；可选基于大模型的宣传文本解析入库。

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 21、Spring Boot 3、Spring Security、JWT、JPA/Hibernate、MySQL 8、Redis |
| 前端 | Vue 3、TypeScript、Vite、Vue Router（`unplugin-vue-router` 文件路由）、Vant 4、Tailwind CSS 4 |
| 容器 | Docker Compose（MySQL、Redis、后端、前端开发服务） |

---

## 仓库结构

```
JobMate/
├── backend/                 # Spring Boot 应用（端口默认 8080）
│   └── src/main/java/cn/edu/zju/cs/jobmate/
│       ├── controllers/     # REST API
│       ├── services/        # 业务逻辑
│       ├── repositories/    # JPA
│       ├── models/          # 实体
│       ├── configs/         # Security、CORS、自定义参数解析等
│       └── dto/             # 请求/响应 DTO
├── frontend/                # Vue SPA（开发端口默认 5173）
│   └── src/
│       ├── pages/           # 按路由组织的页面
│       ├── services/        # 对 /api 的封装
│       └── utils/           # 请求、收藏、返回导航等工具
├── docker-compose.yml       # 一键拉起 MySQL + Redis + 后端 + 前端 dev
├── Dockerfile               # 后端开发镜像（JDK 21 + Maven）
├── init.sql                 # MySQL 初始化（随 compose 挂载）
└── seed_data.sql            # 示例数据（按需手动导入，见下文）
```

---

## 环境要求

- **Docker**：用于 Compose 一键环境（推荐）。
- **本地开发（不用 Docker 跑后端时）**：JDK 21、Maven、Node 20+、pnpm；本机 MySQL 8、Redis，并与 `application.yml` 中的连接信息一致。

---

## 配置说明

### 1. Docker Compose 使用的 `.env`（仓库根目录）

Compose 中 MySQL、Redis 从 `.env` 读取敏感项，**需在项目根目录创建 `.env`**（勿提交真实密码到公开仓库），例如：

```env
MYSQL_ROOT_PASSWORD=你的root密码
REDIS_PASSWORD=你的Redis密码
```

`docker-compose.yml` 里应用容器还传入：

| 变量 | 说明 |
|------|------|
| `DB_*` | 连接 Compose 内 MySQL |
| `REDIS_*` | 连接 Compose 内 Redis |
| `OPENAI_API_KEY` | 可选；管理员「粘贴宣传文本 → AI 解析」需要 |
| `OPENAI_BASE_URL` / `OPENAI_MODEL` | 可选；兼容 OpenAI 协议的网关与模型 |

### 2. 后端 `backend/src/main/resources/application.yml`

- **数据源**：`DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USER`、`DB_PASSWORD`（默认示例与 compose 一致）。
- **Redis**：`REDIS_HOST`、`REDIS_PORT`、`REDIS_PASSWORD`。
- **JWT**：`app.jwt.secret`、`app.jwt.expiration`（生产务必替换）。
- **管理员口令**：`app.admin.secret`（用于管理员登录等逻辑，生产务必替换）。
- **CORS**：`app.cors.allowed-origins`（默认包含本机 Vite `5173`；若前端域名/端口变化需同步）。
- **LLM**：`app.llm.*` 与上述环境变量对应。

### 3. 前端环境变量

| 变量 | 说明 |
|------|------|
| `VITE_API_BASE_URL` | 开发时代理上游：在 `vite.config.ts` 中会去掉 `/api` 后缀后作为 `server.proxy['/api'].target`。Compose 内设为 `http://app:8080`；本机直连后端一般为 `http://127.0.0.1:8080`。 |

前端请求统一走 **`/api`**（由 Vite 代理到后端根地址，实际访问后端 `/api/...`）。

### 4. 示例数据

`seed_data.sql` 等种子脚本通常需在本机或容器内对 `jobmate_db` **手动执行**（视你们部署约定而定），用于演示职位、公司、活动等。

---

## 启动方式

### 方式 A：Docker Compose（推荐）

```bash
# 1. 准备好根目录 .env（见上文）
docker compose up -d mysql redis
# 等待 healthy 后
docker compose up app frontend
```

- **后端 API**：`http://localhost:8080`
- **前端开发服**：`http://localhost:5173`（容器内 `pnpm dev`，代码通过 volume 挂载）

应用容器工作目录在 `backend`，命令为 `mvn spring-boot:run`；首次拉依赖可能较慢。

### 方式 B：本机分别启动

1. 启动 MySQL、Redis，并创建库与用户（与 `application.yml` 一致）。
2. **后端**：`cd backend && ./mvnw spring-boot:run`（或 IDE 运行主类）。
3. **前端**：`cd frontend && pnpm install && pnpm dev`，并设置 `VITE_API_BASE_URL` 指向后端（如 `http://127.0.0.1:8080`）。

---

## 已实现功能（概览）

### 学生/访客侧（需登录后使用；路由层无 token 会跳转登录）

- **首页**：入口、统计、搜索跳转。
- **资讯**：职位 / 活动 Tab 列表；招聘类型与学历组合筛选；URL 同步筛选与 Tab，便于从详情返回恢复状态。
- **搜索**：关键词搜职位与活动；**公司关键词**匹配企业列表，点选后查看该公司全部职位；可与关键词组合。
- **详情**：职位详情、活动详情；职位内可跳转「该公司全部职位」；活动状态展示与布局优化。
- **我的**：登录/注册、收藏列表、设置等；**浙大登录**按钮组件（与统一身份对接按你们环境配置）。
- **返回逻辑**：详情页通过 `from` 查询参数记录来源，返回时优先回到进入前的列表/搜索页。

### 管理员侧

- **管理员登录**（独立入口与校验）。
- **后台首页**：跳转职位管理、活动管理。
- **职位 / 活动 CRUD**：列表、分页加载、发布/编辑弹窗（与活动管理**同宽**适配）、删除。
- **AI 解析宣传文本**（需配置 `OPENAI_API_KEY`）：解析为结构化字段并回填表单（具体以 `AdminPromotionParseController` 为准）。

### 后端 API 模块（控制器）

- `AuthenticationController`：登录、JWT。
- `UserController`：注册等用户接口。
- `JobInfoController`：职位分页、搜索（支持 `keyword`、`recruit_type`、`company_id` 等）。
- `ActivityInfoController`：活动分页与搜索。
- `CompanyController`：企业与搜索。
- `FavoriteController`：收藏。
- `AdminPromotionParseController`：管理员宣传解析。

### 安全与鉴权（设计要点）

- **无状态会话**：JWT + `Authorization: Bearer`；Redis 侧可做 token 黑名单（登出等）。
- **Spring Security**：除 `/api/auth/login`、`/api/users/register` 外，默认需已认证；无 Bearer 时 JWT 过滤器放行链路由，但匿名访问受保护接口会由入口点返回未认证（前端配合跳转登录）。
- **管理员**：角色 `ADMIN`；前端底栏对管理员展示「后台」入口。

---

## 前后端设计说明

### 整体架构

- **前后端分离**：前端 SPA，后端仅提供 JSON API（`/api/**`）。
- **约定**：统一响应包装（如 `code`、`message`、`data`）；分页常见字段为 `content`、`total`、`page` 等（以后端 `PageResponse` 为准）。
- **跨域**：后端 CORS 白名单；开发环境由 Vite 将浏览器对同源 `/api` 的请求代理到后端。

### 后端分层

- **Controller**：参数校验（`@Valid`）、分页 DTO 常通过自定义 **`PageRequestParamResolver`** 将 `snake_case` 查询参数转为驼峰并绑定到 `PageRequest` 及其子类（如 `JobInfoQueryRequest`）。
- **Service**：业务规则、事务；职位搜索在有关键词时走 **MySQL 全文 + LIKE** 等组合查询，可按公司 ID 收窄。
- **Repository**：Spring Data JPA + 少量 `@Query` 原生 SQL。
- **模型**：`JobInfo`、`ActivityInfo`、`Company`、`User` 等，与 MySQL 表对应；`ddl-auto` 开发期多为 `update`（生产请改为显式迁移策略）。

### 前端分层

- **路由**：`src/pages/**` 文件即路由（`unplugin-vue-router`）。
- **请求**：`src/utils/request.ts` 封装 `fetch`，统一附加 Token、错误与 Toast；业务调用在 `src/services/*.ts`。
- **UI**：移动端优先（Vant + Tailwind），管理列表使用 `max-w` + `vw` 限制大屏宽度。

### 数据流简述

浏览器 → Vite 代理 `/api` → Spring Security 过滤器链（审计、登录、JWT）→ Controller → Service → JPA → MySQL；会话相关或黑名单等使用 Redis。

---

## 生产部署注意（简要）

- 修改 **JWT / 管理员密钥 / 数据库密码**，关闭或保护 **`/monitor/**`**。
- 将 **`spring.jpa.hibernate.ddl-auto`** 改为适合生产的策略（如 `validate` + Flyway/Liquibase）。
- **CORS** 与 **HTTPS**、前端实际域名对齐。
- 前端 `pnpm build` 产物由 Nginx 等静态托管，并把 `/api` 反代到网关或 Spring Boot。

---

## 许可与贡献

内部或课程项目请按组织要求补充 License 与贡献指南；当前仓库若未声明，默认保留原有约定。
