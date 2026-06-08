# 中医问诊与养生平台

这是中医问诊与养生平台的完整项目仓库。仓库同时容纳后端、未来的前端和项目文档。

## 目录结构

```text
tcm-consultation-platform/
├─ backend/   Spring Boot 后端项目
├─ frontend/  前端项目预留目录
├─ docs/      项目说明、开发路线和 Git/GitHub 伴学指南
└─ README.md  整个项目的入口说明
```

## 后端结构

```text
backend/
├─ pom.xml
├─ README.md
└─ src/
   ├─ main/   正式运行代码和资源
   └─ test/   自动化测试代码
```

`src/test` 不是冗余目录。它用于编写单元测试和集成测试，不会作为正式业务代码打包运行。

## 当前状态

- 后端：课程提供的 Spring Boot Starter 骨架，等待逐步实现。
- 前端：尚未选择或初始化前端技术栈。
- Git：尚未初始化，建议在仓库根目录初始化，以统一管理前后端和文档。

## 开始实战

优先阅读：

1. `docs/中医问诊与养生平台_Git与GitHub双线实战指南.md`
2. `docs/PROJECT_DESCRIPTION_REPORT.md`
3. `docs/FRONTEND_PLAN.md`
4. `backend/README.md`

运行 Maven 命令时，可以先进入后端目录：

```powershell
cd backend
mvn compile
```

也可以留在仓库根目录并指定 Maven 项目文件：

```powershell
mvn -f backend/pom.xml compile
```
