FROM openjdk:17-alpine as jre-build
USER root
# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules ALL-MODULE-PATH \
         --output /javaruntime

# Target image
FROM alpine
USER root
WORKDIR /mayi
MAINTAINER zf1976 <verticle@foxmail.com>
LABEL name=mayi-auth
LABEL url=https://github.com/zf1976/mayi
# env
ENV LANG C.UTF-8
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME
ENV JAR_FILE=./mayi-auth/build/libs/mayi-auth-latest.jar
ENV CONFIG=./bootstrap.yml
COPY $JAR_FILE ./target.jar
COPY $CONFIG ./bootstrap.yml
# depoly
ENV JVM_OPTS="-Xms128m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar /mayi/target.jar --spring.config.additional-location=/mayi/bootstrap.yml