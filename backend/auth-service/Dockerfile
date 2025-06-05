FROM maven AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:25-slim

COPY --from=builder /app/target/*.jar /app/app.jar

CMD ["java","-jar","/app/app.jar"]