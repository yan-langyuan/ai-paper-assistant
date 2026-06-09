# AI论文助手 —— 从零开始启动教程

> 适用版本：V1.0.0 MVP | 最后更新：2026-05-27

---

## 一、准备工作

### 1.1 环境要求

| 工具 | 最低版本 | 检查命令 | 说明 |
|------|:---:|------|------|
| **JDK** | 17+ | `java -version` | 必须 JDK 17，否则 SpringBoot 3.2 无法运行 |
| **Maven** | 3.6+ | `mvn -version` | 后端构建工具 |
| **Node.js** | 18+ | `node -v` | 推荐 v22.14.0 |
| **npm** | 9+ | `npm -v` | 随 Node.js 自带 |
| **Git** | 2.30+ | `git --version` | 拉取代码 |

### 1.2 克隆项目

```bash
git clone git@github.com:yan-langyuan/ai-paper-assistant.git
cd ai-paper-assistant
```

---

## 二、后端启动

### 2.1 配置 JDK 17（最重要的一步）

Maven 默认可能使用系统的旧 JDK，而 SpringBoot 3.2.5 **强制要求 JDK 17**。

**Windows PowerShell:**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.3"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -version   # 确认输出为 17.0.3
```

**Windows Git Bash:**
```bash
export JAVA_HOME="/c/Program Files/Java/jdk-17.0.3"
export PATH="$JAVA_HOME/bin:$PATH"
java -version   # 确认输出为 17.0.3
```

> 如果你的 JDK 17 安装在其他路径，请替换为正确的路径。可以用 `ls "C:\Program Files\Java\"` 查看已安装的 JDK 版本。

### 2.2 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

首次运行 Maven 会自动下载依赖（约 2-3 分钟），之后启动仅需 3-5 秒。

看到以下输出表示启动成功：

```
Started AiPaperApplication in 3.xxx seconds
Tomcat started on port 8080 (http)
```

后端运行在 **http://localhost:8080**。

无需额外安装 MySQL——项目使用 H2 内嵌数据库（文件模式），数据保存在 `backend/data/` 目录下，重启不丢失。

### 2.3 验证后端

打开浏览器访问 http://localhost:8080/h2-console，使用以下信息登录 H2 控制台：

| 参数 | 值 |
|------|------|
| JDBC URL | `jdbc:h2:file:./data/aipaper;DB_CLOSE_DELAY=-1;MODE=MySQL` |
| 用户名 | `sa` |
| 密码 | （留空） |

能成功登录并看到表结构，说明数据库初始化正常。

### 2.4 后端常见问题

| 问题 | 原因 | 解决 |
|------|------|------|
| `UnsupportedClassVersionError: class file version 61.0` | Maven 用了 JDK 11 | 按 2.1 节设置 JAVA_HOME 指向 JDK 17 |
| `Port 8080 was already in use` | 端口被上次残留进程占用 | `netstat -ano \| findstr 8080` 找到 PID，`taskkill /F /PID <PID>` 杀掉 |
| `CommandAcceptanceException` | H2 保留关键字问题 | 删除 `backend/data/` 目录后重试 |

---

## 三、前端启动

### 3.1 安装依赖

```bash
cd frontend/ai-paper-frontend
npm install
```

首次安装约 1-2 分钟。

### 3.2 启动前端开发服务器

```bash
npm run dev
```

看到以下输出表示启动成功：

```
VITE v6.x  ready in xxx ms
➜  Local:   http://localhost:5173/
```

前端运行在 **http://localhost:5173**。

### 3.3 验证前端

浏览器打开 http://localhost:5173，应该看到 AI论文助手首页，包含：

- 顶部导航栏（暖色调，衬线字体 Logo）
- 四个功能入口卡片（文献/格式/降重/PPT）
- "登录"按钮

### 3.4 前端常见问题

| 问题 | 原因 | 解决 |
|------|------|------|
| `npm: command not found` | Node.js 未安装 | 从 https://nodejs.org 下载 LTS 版本安装 |
| `Cannot find module 'xxx'` | 依赖未安装完整 | 删除 `node_modules/` 和 `package-lock.json`，重跑 `npm install` |
| 页面空白 | 后端未启动或端口不对 | 确认后端在 8080 端口运行 |

---

## 四、前后端联调

### 4.1 同时启动

需要开两个终端窗口：

**终端 1（后端）：**
```bash
cd ai-paper-assistant/backend
export JAVA_HOME="/c/Program Files/Java/jdk-17.0.3"
mvn spring-boot:run
```

**终端 2（前端）：**
```bash
cd ai-paper-assistant/frontend/ai-paper-frontend
npm run dev
```

### 4.2 注册账号并体验完整流程

1. 浏览器打开 http://localhost:5173
2. 点击右上角"登录" → 切换到"注册"标签
3. 填写信息（用户名、密码、邮箱、年级、专业）
4. 注册成功后自动登录，开始使用

### 4.3 推荐的首个体验流程

```
首页 → 智能文献助手 → 导入文献（上传一份PDF）
    → 查看自动生成的结构化摘要
    → 添加参考文献格式化列表
    → 格式排版 → 选择GB/T 7714 → 预览导出Word

首页 → 降重改写助手 → 粘贴一段文本
    → 开始检测 → 查看三色高亮结果
    → 点击某个高亮句 → 查看3个改写方案（A/B/C）
    → 选择一个方案 → 手动编辑 → 重新检测
```

---

## 五、项目结构速览

```
ai-paper-assistant/
├── backend/                              # Java SpringBoot 后端
│   ├── pom.xml                           # Maven 配置（依赖列表）
│   ├── src/main/java/com/aipaper/
│   │   ├── AiPaperApplication.java       # 启动入口
│   │   ├── config/                       # CORS、Security、JWT 配置
│   │   ├── controller/                   # REST API 控制器（4个）
│   │   ├── service/                      # 业务逻辑接口 + 实现
│   │   ├── model/                        # JPA 实体（4张表）
│   │   ├── repository/                   # 数据访问层
│   │   └── dto/                          # 请求/响应对象
│   └── src/main/resources/
│       └── application.yml               # 数据库、AI API、端口配置
│
├── frontend/ai-paper-frontend/           # Vue 3 前端
│   ├── src/
│   │   ├── views/                        # 页面（10个）
│   │   ├── components/                   # 通用组件（9个）
│   │   ├── stores/                       # Pinia 状态管理（3个）
│   │   ├── api/                          # 后端 API 封装（5个）
│   │   ├── router/                       # 路由配置
│   │   └── style.css                     # 全局样式 + Tailwind
│   ├── tailwind.config.js                # 设计系统 Token
│   ├── vite.config.js                    # Vite 构建配置
│   └── index.html                        # 入口 HTML
│
└── docs/                                 # 产品文档
    ├── PRD文档.md                         # 产品需求文档（核心）
    ├── 需求梳理清单.md                     # 完整需求池 + 优先级
    ├── 产品功能地图.md                     # 思维导图 + 功能结构
    ├── 业务流程图.md                       # 页面流程 + 业务逻辑
    ├── 产品原型说明.md                     # 原型设计蓝本
    ├── 用户调研报告.md                     # 200份问卷分析
    ├── 竞品分析报告.md                     # 4款竞品对比
    ├── 项目搭建操作方案.md                 # 搭建详细步骤
    └── 报错                              # 已知问题与修复记录
```

---

## 六、核心 API 一览

| 方法 | 路径 | 说明 | 需要登录 |
|------|------|------|:---:|
| POST | `/api/auth/register` | 注册 | 否 |
| POST | `/api/auth/login` | 登录，返回 JWT | 否 |
| POST | `/api/literature/upload` | 上传 PDF 文献 | 是 |
| GET | `/api/literature` | 文献列表 | 是 |
| GET | `/api/literature/{id}` | 文献详情 + AI 摘要 | 是 |
| DELETE | `/api/literature/{id}` | 删除文献 | 是 |
| POST | `/api/literature/{id}/summary` | 手动触发摘要生成 | 是 |
| POST | `/api/reference/format` | 批量格式化参考文献 | 是 |
| GET | `/api/reference/standards` | 获取支持的格式标准 | 是 |
| POST | `/api/paraphrase/detect` | 粘贴文本 → 重复检测 | 是 |
| POST | `/api/paraphrase/rewrite` | 生成改写方案（A/B/C） | 是 |

**认证方式**：在请求头中携带 `Authorization: Bearer <token>`，前端已由 Axios 拦截器自动处理。

---

## 七、AI 服务配置

项目通过 `application.yml` 中的 `ai.api.*` 配置连接大模型：

```yaml
ai:
  api:
    url: https://api.openai.com/v1/chat/completions   # API 地址
    key: ${AI_API_KEY:demo-key}                       # 从环境变量读取
    model: gpt-4o-mini                                 # 模型名称
```

设置 AI API Key（二选一）：

**方式一：环境变量（推荐）**
```bash
export AI_API_KEY="your-api-key-here"
```

**方式二：直接修改配置文件**
编辑 `backend/src/main/resources/application.yml`，将 `demo-key` 替换为你的实际 Key。

> 如果没有配置有效的 AI API Key，文献摘要和改写功能会失败，但文献导入、解析、参考文献格式化仍可正常使用。

---

## 八、技术决策说明

| 决策 | 选择 | 原因 |
|------|------|------|
| 数据库 | H2（文件模式） | 零安装，开箱即用，MySQL 兼容模式 |
| 认证 | JWT | 无状态，适合前后端分离 |
| PDF 解析 | PDFBox 3.0 | Apache 开源，纯文本提取 |
| 参考文献格式化 | 纯 Java 规则引擎 | 零 AI 幻觉风险，100% 格式正确 |
| 前端样式 | Tailwind CSS | 快速开发，设计系统一致 |
| 设计风格 | Editorial（编辑风格） | 暖墨水 + 琥珀强调色，区别于通用 SaaS UI |

---

## 九、下一步

MVP 完成后，可按以下路线迭代：

- **V1.1**：真实文献检索（知网 API）、格式一致性检查、学校模板套用、PPT 大纲生成
- **V1.2**：多文献观点对比、核心观点自动提炼、分年级引导模式
- **后续**：微信小程序、英文论文支持、付费增值
