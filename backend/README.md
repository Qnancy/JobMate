# JobMate Backend

Hello！欢迎来到 JobMate 后端部分！

## Abstract

后端部分的技术栈选型为

- Java 作为主要编程语言
- SpringBoot 作为后端主要开发框架
- MySQL 作为后端数据库
- Redis 作为后端缓存中间件
- Git + GitHub 作为版本控制系统
- Docker 作为容器打包与部署工具
- ... 等待更多补充

后端部分总体上会参考已有项目的经验，[仓库地址](https://github.com/lEEExp3rt/OpenCourse)

## Development Environment

目前，所有的开发准备工作已经就绪，作为开发人员，你可以启动你的开发了！

### Dev Locally

如果在本地开发，确保正确配置 JDK-21 + mvn-3.9.11 + MySQL-8.0

mvn 安装可以直接运行 `/backend/mvnw` 命令（或 `mvnw.cmd`，如果你是 Windows 系统），首次运行会自动为你的环境安装项目所需的 `mvn`，后续也可以直接运行这个 `mvnw` 以替代 `mvn` 命令

```shell
$ cd backend/
$ chmod +x ./mvnw

# 查看 mvn 版本
$ ./mvnw -v

# 安装项目依赖
$ ./mvnw dependency:resolve
$ ./mvnw dependency:go-offline

# 启动项目
$ ./mvnw spring-boot:run
```

### Dev Using Docker Containers

如果你不想进行本地的环境部署，或者本地环境部署有问题，可以使用提供的开发容器

开发容器基于 docker-compose 进行多容器编排，主开发容器为 jobmate-app，内部已经打包好 Java + mvn 等开发工具和环境，并配置了必要的开发工具

```shell
# 在 /backend 下构建开发容器并启动
$ docker compose up --build -d

# 查看容器
$ docker container ls

# 从命令行进入开发容器
$ docker exec -it jobmate-app-1 fish
```

如果你使用 VSCode 作为开发编辑器，可以在扩展市场搜索 `ms-vscode-remote.remote-containers` 以安装 Dev Containers 扩展，用于便捷连接开发容器直接进行开发

进入开发容器后，同样需要运行 `mvn` 以安装项目依赖
