# Target image
FROM adoptopenjdk:11-jre-openj9
USER root
WORKDIR /mayi
MAINTAINER zf1976 <verticle@foxmail.com>
LABEL name=mayi-visual-admin
LABEL url=https://github.com/zf1976/mayi
# env
ENV LANG C.UTF-8
ENV JAR_FILE=./mayi-visual/mayi-visual-admin/build/libs/mayi-visual-admin-latest.jar
COPY $JAR_FILE ./target.jar
# depoly
ENV JVM_OPTS="-Xms128m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar /mayi/target.jar