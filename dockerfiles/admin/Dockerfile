# Debian --- OpenJ9-Jlink
FROM gradle:jdk11-openj9 as gradle-build
USER root
WORKDIR /mayi
# Cache Deps
COPY ./gradle ./gradle
COPY ./settings.gradle ./settings.gradle
COPY ./gradle.properties ./gradle.properties
COPY ./gradlew ./gradlew
COPY ./gradlew.bat ./gradlew.bat
COPY ./lombok.config ./lombok.config
COPY ./build.gradle ./build.gradle
COPY ./mayi-auth ./mayi-auth
COPY ./mayi-common ./mayi-common
COPY ./mayi-gateway ./mayi-gateway
COPY ./mayi-upms ./mayi-upms
COPY ./mayi-visual ./mayi-visual
RUN ./gradlew -b ./mayi-upms/mayi-upms-biz/build.gradle bootJar --info
# Rename mayi-upms-biz Jar
RUN mv ./mayi-upms/mayi-upms-biz/build/libs/mayi-upms-biz-latest.jar ./target.jar

# Target image
FROM adoptopenjdk:11-jre-openj9
USER root
WORKDIR /mayi
MAINTAINER zf1976 <verticle@foxmail.com>
LABEL name=mayi-upms-biz
LABEL url=https://github.com/zf1976/mayi
# depes env
ENV LANG C.UTF-8
ENV JAR_FILE=/mayi/target.jar
COPY --from=gradle-build $JAR_FILE ./target.jar
# depoly
EXPOSE 7777
EXPOSE 5555
ENV JVM_OPTS="-Xms256m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java $JVM_OPTS -jar /mayi/target.jar