# Etapa de build
FROM maven:3.8.7-eclipse-temurin-17 AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto
COPY pom.xml .
COPY src ./src

# Baixa as dependências e faz o build
RUN mvn clean package -DskipTests

# Etapa de execução
FROM eclipse-temurin:17-jre-alpine

# Cria um usuário não-root para segurança
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