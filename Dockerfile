FROM --platform=$TARGETPLATFORM gcr.io/distroless/java21-debian12

WORKDIR /app

COPY target/ftpd-*.jar app.jar

EXPOSE 21

# -e JAVA_TOOL_OPTIONS="-Xmx512m"
ENTRYPOINT ["java","-jar","app.jar"]