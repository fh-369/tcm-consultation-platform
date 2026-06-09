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

行为测试：

```powershell
npm test
```

## 环境变量

复制 `.env.example` 为本机 `.env` 后可修改后端地址：

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

`.env` 不应提交到 Git。

## 当前路由

患者端：

- `/`
- `/consultation/new`
- `/consultation/my`
- `/knowledge`
- `/recipes`
- `/profile`

认证：

- `/login/patient`
- `/login/admin`
- `/register`

后台端：

- `/admin`
- `/admin/consultations`
- `/admin/knowledge`
- `/admin/recipes`
- `/admin/export`

Frontend Phase 2 已加入患者登录、后台登录、患者注册、Pinia 登录会话、Bearer Token 请求头和角色路由守卫。真实问诊及内容接口联调属于后续阶段。
