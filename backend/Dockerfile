FROM maven:3.8.7-eclipse-temurin-17 AS build

WORKDIR /app

ARG M2_HOME_ARG

COPY pom.xml .

# RUN --mount=type=cache,target=/root/.m2 \
#     mvn dependency:go-offline

COPY src ./src

RUN  mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Define o comando de entrada
ENTRYPOINT ["java", "-jar", "app.jar"]