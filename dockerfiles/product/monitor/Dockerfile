# Target image
FROM adoptopenjdk:11-jre-openj9
USER root
WORKDIR /mayi
MAINTAINER zf1976 <verticle@foxmail.com>
LABEL name=mayi-admin-monitor
LABEL url=https://github.com/zf1976/mayi
# depes env
ENV LANG C.UTF-8
ENV JAR_FILE=./mayi-visual/mayi-admin-monitor/build/libs/mayi-admin-monitor-latest.jar
COPY $JAR_FILE ./target.jar
# depoly
EXPOSE 4444
ENV JVM_OPTS="-Xms256m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar /mayi/target.jar