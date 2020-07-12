# Build stage
FROM maven:3.3.9-jdk-8-alpine AS build-env

# Create app directory
WORKDIR /app

COPY src ./src
COPY pom.xml ./
RUN mvn clean package -DskipTests -DjarFinalName=${JAR_FILE_NAME}

# Package stage
FROM maven:3.3.9-jdk-8-alpine
WORKDIR /app
COPY --from=build-env /app/target/${JAR_FILE_NAME}.jar ./
COPY ${APM_AGENT_JAR} ./
ENV APM_AGENT_OPTS="-javaagent:${APM_AGENT_JAR} -Delastic.apm.service_name=${app_name} -Delastic.apm.application_packages=${APM_PACKAGE} -Delastic.apm.server_urls=${APM_SERVER}"

ENTRYPOINT java ${APM_AGENT_OPTS} ${JAVA_OPTS} -jar ${JAR_FILE_NAME}.jar