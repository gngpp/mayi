# Target image
FROM adoptopenjdk:11-jre-openj9
USER root
WORKDIR /mayi
MAINTAINER zf1976 <verticle@foxmail.com>
LABEL name=mayi-gateway
LABEL url=https://github.com/zf1976/mayi
# env
ENV LANG C.UTF-8
ENV JAR_FILE=./mayi-gateway/build/libs/mayi-gateway-latest.jar
ENV CONFIG=./bootstrap.yml
COPY $JAR_FILE ./target.jar
COPY $CONFIG ./bootstrap.yml
# depoly
ENV JVM_OPTS="-Xms128m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar /mayi/target.jar --spring.config.additional-location=/mayi/bootstrap.yml