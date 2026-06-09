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

Frontend Phase 4 已加入真实后台工作台、问诊管理筛选、详情查看以及状态和医生备注更新。内容接口属于后续阶段。
