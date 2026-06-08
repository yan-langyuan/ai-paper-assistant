# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a product planning and documentation project for **AI论文助手** (AI Thesis Assistant) — a lightweight tool targeting college students to help with three core thesis-writing pain points: literature summarization, reference formatting, and paraphrasing for plagiarism reduction.

The project is in the **requirements/planning phase** — no application code exists yet. The repo contains user research, competitive analysis, requirements specification, flow diagrams, and a PRD.

## Repository Structure

The workflow follows a linear product planning pipeline:

```
需求调研.txt                    → Task: generate survey questionnaire
  → AI论文助手需求调研问卷.md     Output: 30-question survey

Dysj.py + 数据分析.txt           → Task: generate + analyze 200 mock responses
  → ai_paper_assistant_survey_200.csv  Mock data
  → analysis.py                  Analysis script
  → 用户调研报告.md               Output: user research report

竞品分析诉求.txt + 用户调研报告   → Task: competitive analysis
  → 竞品分析报告.md               Output: 4-product comparison (千笔AI, DeepSeek, 沁言学术, 豆包)

需求分析诉求.txt + 调研+竞品      → Task: requirements analysis
  → 需求梳理清单.md               Output: 26-item requirement pool, priority matrix, flow diagrams

执行方案落地.txt + 需求梳理清单    → Task: implementation planning
  → 产品功能地图.md               Output: mind map + functional structure
  → 业务流程图.md                 Output: page flows + business logic flows (12 error scenarios)
  → 产品原型说明.md               Output: 17-page wireframe spec with interaction annotations
  → PRD文档.md                   Output: comprehensive PRD (10 chapters)
```

## Key Documents

- **[PRD文档.md](PRD文档.md)** — The final integrated PRD. This is the single source of truth for MVP scope (6 P0 features, 30 person-days), acceptance criteria, design constraints, and non-functional requirements.
- **[需求梳理清单.md](需求梳理清单.md)** — Complete requirement inventory with priority matrix (value-cost quadrants), module breakdowns, and data flow diagrams.
- **[竞品分析报告.md](竞品分析报告.md)** — Competitive landscape: 千笔AI (broad but trust issues), DeepSeek (strong reasoning, no productization), 沁言学术 (compliance king), 豆包 (free but shallow).
- **[用户调研报告.md](用户调研报告.md)** — 200-response survey analysis showing top 3 pain points: reference formatting (62.5%), literature summarization (57.5%), sentence-level paraphrasing (52.5%).

## MVP Core Design Constraints

When making changes, preserve these 5 hard constraints (defined in PRD Ch.7):

1. **DC-01**: No "one-click rewrite all" button in the paraphrasing module — this is the core differentiator
2. **DC-02**: All AI-generated content must display "AI生成，仅供参考" disclaimer
3. **DC-03**: Plagiarism detection must run locally (browser-side), text never uploaded
4. **DC-04**: Rewrite suggestions are "advice" not "auto-replace" — user retains editing control
5. **DC-05**: AI usage warnings are reminders, not blockers; no punitive language



# 全局设置
## 语言设置
始终使用中文回复，所有对话、解释和代码注释都应使用中文。

# 项目结构

```
D:\FrontEndProject\firstCc\
├── CLAUDE.md                       # 本文件
├── *.txt                           # 各阶段任务描述文件
├── *.md                            # 产品文档产出（调研/分析/需求/PRD等）
├── *.py / *.csv                    # 调研数据生成与分析脚本
│
└── ai-paper-assistant/             # 主项目目录
    ├── docs/                       # 产品文档（PRD、原型、流程图等）
    │   └── 项目搭建操作方案.md       # ← 从0到1搭建详细步骤
    ├── backend/                    # Java SpringBoot 后端（端口8080）
    └── frontend/                   # Vue 3 前端（端口8081）
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 (Composition API) + Vue Router + Pinia + Tailwind CSS + Vite |
| 后端 | Java 17 + SpringBoot 3.2 + Spring Data JPA + Spring Security |
| 数据库 | MySQL 8.0 |
| 构建 | Maven 3.6+ (后端) / npm (前端) |
| IDE | VSCode |
| AI | 大模型API（多模型可切换路由） |
| PDF | PDFBox 3.0 (后端) |
| Word | Apache POI 5.2 (后端) |

## 常用命令

```bash
# === 重要：后端启动前必须先设置 JAVA_HOME ===
# Maven 3.6.1 默认使用系统 JDK 11，但 SpringBoot 3.2.5 要求 JDK 17
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.3"   # PowerShell
export JAVA_HOME="/c/Program Files/Java/jdk-17.0.3"    # Git Bash

# 后端
cd ai-paper-assistant/backend
mvn spring-boot:run          # 启动开发服务器 (localhost:8080)
mvn test                     # 运行测试

# 前端
cd ai-paper-assistant/frontend/ai-paper-frontend
npm run dev                  # 启动开发服务器 (localhost:5173)
npm run build                # 生产构建

# 环境
java -version                # 应为 JDK 17
node -v                      # 应为 v22.14.0
```

## 关键设计约束（来自 PRD 第七章）

编写任何代码时必须遵守：

1. **DC-01**: 降重模块禁止提供"一键改写全文"按钮 — 核心差异化
2. **DC-02**: 所有AI生成内容必须标注"AI生成，仅供参考"
3. **DC-03**: 降重检测优先在浏览器端本地完成（Transformers.js），文本不上传服务器
4. **DC-04**: 改写方案以"建议"形式呈现，用户手动选择并编辑
5. **DC-05**: AI使用风险预警仅提示不拦截，文案免说教