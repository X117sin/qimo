# 中医方证速查与笔记系统

## 项目介绍

本项目是一个基于Spring Boot与MySQL的中医方证速查与笔记系统，旨在帮助中医学生快速检索条文、标注重点、记录学习进度，提高学习效率。

## 技术栈

- 前端：Angular 17
- 后端：Spring Boot 3.x, Spring MVC, Spring Data JPA
- 数据库：MySQL 8.x (InnoDB引擎，UTF8MB4编码)
- 安全：Spring Security + JWT实现登录鉴权
- 构建：Maven（配置多环境）
- 部署：Docker Compose

## 项目依赖

### 核心依赖

- Spring Boot 3.1.5
- Spring Data JPA - 数据访问层
- Spring Security - 安全框架
- Spring Validation - 数据验证

### 数据库相关

- MySQL Connector Java - MySQL驱动
- Flyway - 数据库版本控制与迁移工具

### 安全相关

- JJWT (0.11.5) - JWT令牌生成与验证

### 开发工具

- Lombok - 减少样板代码
- Spring Boot DevTools - 开发热重载

### 测试工具

- Spring Boot Test - 单元测试与集成测试
- JaCoCo - 测试覆盖率分析（配置要求覆盖率不低于60%）

## 功能特性

1. 用户注册/登录（使用JWT实现会话管理）
2. 条文管理（CRUD操作、分页查询、条件搜索）
3. 错题本功能（记录答题错误的条文，支持重做）
4. 学习进度统计（已学习条文数量、最近学习时间等）
5. 基础管理后台（管理员维护用户和条文数据）

## 项目结构

```
tcm-notes/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── tcm/
│   │   │           └── notes/
│   │   │               ├── config/        # 配置类
│   │   │               ├── controller/    # 控制器
│   │   │               ├── dto/           # 数据传输对象
│   │   │               ├── entity/        # 实体类
│   │   │               ├── exception/     # 异常处理
│   │   │               ├── repository/    # 数据访问层
│   │   │               ├── security/      # 安全相关
│   │   │               ├── service/       # 业务逻辑层
│   │   │               └── TcmNotesApplication.java  # 启动类
│   │   └── resources/
│   │       ├── db/migration/             # Flyway迁移脚本
│   │       │   └── V1__init_schema.sql   # 初始化数据库结构
│   │       └── application.properties     # 配置文件
│   └── test/                              # 测试代码
├── Dockerfile                             # Docker镜像构建文件
├── docker-compose.yml                     # Docker Compose配置
└── pom.xml                                # Maven配置
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Docker & Docker Compose (可选，用于容器化部署)

### 数据库设置

1. 创建MySQL数据库：

```sql
CREATE DATABASE tcm_notes DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 配置数据库连接：

编辑 `src/main/resources/application.properties` 文件，设置数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tcm_notes?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=123456
```

### 数据库迁移

项目使用Flyway进行数据库版本控制，初始化脚本位于 `src/main/resources/db/migration/V1__init_schema.sql`，包含以下表结构：

- `users` - 用户信息表
- `passages` - 条文内容表
- `notes` - 用户笔记表
- `favorites` - 用户收藏表
- `study_records` - 学习记录表
- `wrong_answers` - 错题本表

应用启动时，Flyway会自动执行迁移脚本，无需手动执行SQL。

### 本地开发运行

1. 克隆项目到本地
2. 使用Maven构建项目：

```bash
mvn clean package
```

3. 运行应用：

```bash
java -jar target/tcm-notes-0.0.1-SNAPSHOT.jar
```

或者使用Maven直接运行：

```bash
mvn spring-boot:run
```

应用将在 http://localhost:8080/api 上运行

### Docker部署

1. 构建项目：

```bash
mvn clean package -DskipTests
```

2. 使用Docker Compose启动应用：

```bash
docker-compose up -d
```

这将启动MySQL数据库和应用服务，应用将在 http://localhost:8080/api 上运行。

#### Docker环境配置说明

- MySQL数据库将在容器内运行，数据存储在Docker卷中
- 应用容器将等待MySQL容器健康检查通过后再启动
- 应用将使用环境变量连接到MySQL容器
- Docker Compose配置了网络隔离，确保应用与数据库之间的安全通信

#### Docker相关文件

- `Dockerfile` - 定义应用镜像构建过程，基于OpenJDK 17
- `docker-compose.yml` - 定义服务编排，包括MySQL和应用服务

## 开发计划

| 周次 | 主要任务 | 交付物 |
| ---- | -------- | ------ |
| 第1周 | 需求分析、ER图设计、项目骨架搭建 | 需求文档、ER图、初始代码仓库 |
| 第2周 | 用户模块、登录鉴权、数据库迁移脚本 | 用户登录功能、接口文档 |
| 第3周 | 条文CRUD、分页搜索、笔记收藏 | 核心业务接口、单元测试 |
| 第4周 | 学习统计、系统测试、Docker部署 & 文档整理 | 可运行系统、部署脚本、项目说明书 |

## 贡献指南

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建Pull Request

## 许可证

本项目采用MIT许可证 - 详情请参阅 [LICENSE](LICENSE) 文件