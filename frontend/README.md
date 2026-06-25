# 知身问养前端应用

本目录是知身问养平台的 Vue 3 前端应用，一个前端项目同时承载普通用户端、医生端和管理员端页面。

## 技术栈

- Vue 3
- Vite
- JavaScript
- Vue Router
- Pinia
- Axios
- Element Plus
- Markdown-It
- DOMPurify
- Vitest

## 页面范围

### 普通用户端

- 首页：平台介绍与问诊入口
- 在线问诊：创建问诊单，选择科室，填写症状和补充信息
- 我的问诊：查看问诊状态、处理进度、医生回复和补充沟通
- 养生知识：文章列表、筛选、搜索、详情和 Markdown 阅读
- 药膳推荐：药膳列表、季节/体质筛选、搜索和详情
- AI 问答：多轮对话、问诊单上下文、流式回答和站内延伸阅读

### 医生端

- 科室问诊池：查看本科室和综合咨询中的可认领问诊
- 我的问诊：处理已认领或已分配给自己的问诊
- 问诊回复：更新问诊状态，填写医生回复

### 管理员端

- 数据概览：平台运营数据、问诊趋势和风险状态
- 问诊调度：查看全量问诊、调整科室、分配医生
- 用户管理：查看普通用户账号
- 医生管理：审核医生申请、查看医生资料和账号状态
- 养生文章管理：新增、编辑、发布和封面管理
- 药膳管理：新增、编辑、发布和封面管理
- 数据导出：按条件筛选并导出 CSV

## 本地运行

安装依赖：

```powershell
npm install
```

启动开发服务器：

```powershell
npm run dev
```

默认地址：

```text
http://localhost:5173
```

## 环境变量

复制 `.env.example` 为 `.env` 后可以修改后端 API 地址：

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

`.env` 不应提交到 Git。

开发环境下，`vite.config.js` 还会将 `/uploads` 代理到本地后端：

```text
http://localhost:8080
```

## 测试

```powershell
npm test
```

## 生产构建

```powershell
npm run build
```

构建产物输出到：

```text
frontend/dist/
```

`dist/` 是构建产物，不提交到 Git。部署前端静态资源时，可以将 `dist/` 交给 Nginx、静态托管服务或其他 Web 服务器。

## 已知构建提示

当前生产构建可能出现 chunk 体积超过 500 kB 的提示。这不是构建失败。后续可以通过路由级懒加载、动态导入或 Vite/Rollup `manualChunks` 继续优化。

## 与后端联调

联调前需要确认：

1. MySQL 已启动，并已初始化 `tcm_platform` 数据库。
2. 后端已启动在 `http://localhost:8080`。
3. 前端 `.env` 中的 `VITE_API_BASE_URL` 指向后端 `/api`。
4. 浏览器中没有残留过期登录状态。如出现 `401/403`，可以重新登录。
