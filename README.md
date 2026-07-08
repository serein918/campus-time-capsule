# 校园时光胶囊管理系统 (Campus Time Capsule System)

## 项目简介

校园时光胶囊管理系统是一个基于前后端分离架构的Web应用，让大学生可以创建属于自己的时光胶囊，在指定时间开启，也可以选择公开分享，与其他同学交流互动。

## 技术栈

### 后端
- Java 21
- Spring Boot 3.5
- MyBatis-Plus
- MySQL 8
- JWT 认证
- Maven

### 前端
- Vue 3
- Vite
- Element Plus
- Axios
- ECharts
- Pinia

## 项目结构

```
CampusTimeCapsule/
├── capsule-server/          # 后端 Spring Boot 项目
├── capsule-web/             # 前端 Vue3 项目
├── database/                # SQL 数据库脚本
└── README.md
```

## 快速启动

### 1. 数据库初始化

```bash
mysql -u root -p < database/capsule_db.sql
```

### 2. 启动后端

```bash
cd capsule-server
./mvnw spring-boot:run
```

后端运行在 http://localhost:8080

### 3. 启动前端

```bash
cd capsule-web
npm install
npm run dev
```

前端运行在 http://localhost:5173

## 系统账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |

## 功能模块

- 用户注册/登录（JWT认证）
- 时光胶囊 CRUD（创建、查看、修改、删除）
- 公开广场（分页、搜索、分类筛选）
- 评论模块
- 点赞/收藏
- 个人中心
- 管理员后台（用户管理、胶囊管理、公告管理、统计分析）
