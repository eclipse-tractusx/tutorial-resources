FROM eclipse-temurin:23_37-jre-alpine
ARG JAR
ARG OTEL_JAR
ARG ADDITIONAL_FILES

ARG APP_USER=docker
ARG APP_UID=10100

RUN addgroup --system "$APP_USER"

RUN adduser \
     --shell /sbin/nologin \
     --disabled-password \
     --gecos "" \
     --ingroup "$APP_USER" \
     --no-create-home \
     --uid "$APP_UID" \
     "$APP_USER"

USER "$APP_USER"
WORKDIR /app

COPY ${JAR} identityhub.jar
COPY ${OTEL_JAR} opentelemetry-javaagent.jar
COPY ${ADDITIONAL_FILES} ./

HEALTHCHECK NONE

ENTRYPOINT ["java", \
     "-javaagent:/app/opentelemetry-javaagent.jar", \
     "-Dedc.fs.config=/app/configuration.properties", \
     "-Dotel.javaagent.configuration-file=/app/opentelemetry.properties", \
     "-Dotel.metrics.exporter=prometheus", \
     "-Dotel.exporter.prometheus.port=9090", \
     "-Djava.security.egd=file:/dev/urandom", \
     "-jar", \
     "identityhub.jar", \
     "--log-level=debug"]
