# Target image
FROM adoptopenjdk:11-jre-openj9
USER root
WORKDIR /mayi
MAINTAINER zf1976 <verticle@foxmail.com>
LABEL name=mayi-upms-biz
LABEL url=https://github.com/zf1976/mayi
# depes env
ENV LANG C.UTF-8
ENV JAR_FILE=./mayi-upms/mayi-upms-biz/build/libs/mayi-upms-biz-latest.jar
ENV CONFIG=./bootstrap.yml
COPY $JAR_FILE ./target.jar
COPY $CONFIG ./bootstrap.yml
# depoly
EXPOSE 7777
EXPOSE 5555
ENV JVM_OPTS="-Xms256m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar /mayi/target.jar --spring.config.additional-location=/mayi/bootstrap.yml