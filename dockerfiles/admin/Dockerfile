# Debian --- OpenJ9-Jlink
FROM gradle:jdk11 as gradle-build
USER root
WORKDIR /mayi
ARG SOURCE_FILE=./
COPY ${SOURCE_FILE} /mayi
# Dependencies cache
#RUN gradle build
#RUN gradle assemble --info
# Build Jar
RUN gradle -b ./mayi-upms/mayi-upms-biz/build.gradle shadowJar

FROM openjdk:11-jdk as jre-build
USER root
WORKDIR /root
ENV JAR_FILE=/mayi-upms/mayi-upms-biz/build/libs/*.jar
COPY --from=gradle-build ${JAR_FILE} /root/mayi-upms-biz.jar
COPY ./jar-depes.sh /root/jar-depes.sh
# Create a custom Java runtime
RUN bash /root/jar-depes.sh

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
ENV JAR_FILE=/mayi-upms/mayi-upms-biz/build/libs/*.jar
# copy build
COPY --from=jre-build /javaruntime $JAVA_HOME
COPY --from=gradle-build ${JAR_FILE} /root/mayi-upms-biz.jar
# depoly
RUN mkdir /root/logs
EXPOSE 7777
ENV JVM_OPTS="-Xms256m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java ${JVM_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /root/mayi-upms-biz.jar

