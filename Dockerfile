# Debian --- OpenJ9-Jlink
FROM gradle:jdk11 as gradle-build
USER root
WORKDIR /mayi
# build file cache
COPY ./gradle /mayi/gradle
COPY ./mayi-auth /mayi/mayi-auth
COPY ./mayi-common /mayi/mayi-common
COPY ./mayi-gateway /mayi/mayi-gateway
COPY ./mayi-upms /mayi/mayi-upms
COPY ./mayi-visual /mayi/mayi-visual
COPY ./gradle.properties /mayi/gradle.properties
COPY ./build.gradle /mayi/build.gradle
COPY ./settings.gradle /mayi/settings.gradle
# Dependencies cache
RUN gradle assemble --info
# cache config
COPY ./gradlew /mayi/gradlew
COPY ./gradlew.bat /mayi/gradlew.bat
COPY ./lombok.config /mayi/lombok.config
# Build Jar
RUN gradle -b ./mayi-upms/mayi-upms-biz/build.gradle bootJar

FROM openjdk:11-jdk as jre-build
USER root
WORKDIR /root
ENV JAR_FILE=/mayi/mayi-upms/mayi-upms-biz/build/libs/*.jar
ENV JAR_DEPES_SHELL=./jar-depes.sh
COPY --from=gradle-build $JAR_FILE /root/mayi-upms-biz.jar
COPY --from=gradle-build $JAR_DEPES_SHELL /root/jar-depes.sh
# Create a custom Java runtime
RUN chmod u+x ./jar-depes.sh
RUN ./jar-depes.sh .

# Target Image
FROM openjdk:11-jdk
USER root
WORKDIR /root
MAINTAINER zf1976 <verticle@foxmail.com>
LABEL name=mayi-upms-biz
LABEL url=https://github.com/zf1976/mayi
# depes env
ENV LANG C.UTF-8
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
ENV JAR_FILE=/root/mayi-upms-biz.jar
ENV JRE_RUNTIME=/javaruntime
# copy build
COPY --from=jre-build $JRE_RUNTIME $JAVA_HOME
COPY --from=gradle-build /root/mayi-upms-biz.jar /root/mayi-upms-biz.jar
# depoly
RUN mkdir /root/logs
EXPOSE 7777
ENV JVM_OPTS="-Xms256m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar /root/mayi-upms-biz.jar

