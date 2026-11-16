FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/lotamenos-api.jar app.jar

ENV JAVA_OPTS="-Xms128m -Xmx350m -XX:+UseSerialGC -Xss512k -Duser.timezone=America/Sao_Paulo -Doracle.jdbc.timezoneAsRegion=false"
ENV TZ=America/Sao_Paulo

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
