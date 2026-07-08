# ==========================================
# 第一阶段：Maven 构建打包（使用 JDK 21）
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# 1. 单独复制 pom.xml 并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# 第二阶段：轻量级 JRE 运行环境（使用 JRE 21）
# ==========================================
FROM eclipse-temurin:21-jre
WORKDIR /app

# 从构建阶段复制生成的 jar 包
COPY --from=build /app/target/capsule-server-1.0.0.jar app.jar

# 暴露端口（Spring Boot 默认 8080）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]