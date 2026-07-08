# 第一阶段：构建打包
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# 第二阶段：运行
FROM eclipse-temurin:21-jre
COPY --from=build /app/target/capsule-server-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]