# Frontend

中医问诊与养生平台前端使用一个 Vue 应用承载患者端和后台端。

## 技术栈

- Vue 3 + Vite
- JavaScript
- Vue Router
- Pinia
- Axios
- Element Plus

## 本地运行

```powershell
npm install
npm run dev
```

生产构建：

```powershell
npm run build
```

## 环境变量

复制 `.env.example` 为本机 `.env` 后可修改后端地址：

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

`.env` 不应提交到 Git。

## Frontend Phase 1 路由

患者端：

- `/`
- `/consultation/new`
- `/knowledge`
- `/recipes`
- `/profile`

后台端：

- `/admin`
- `/admin/consultations`
- `/admin/knowledge`
- `/admin/recipes`
- `/admin/export`

Phase 1 仅实现前端基础工程、患者顶部导航布局、后台侧边栏布局和占位页面。登录注册、token、权限路由、真实问诊及内容接口联调属于后续阶段。
