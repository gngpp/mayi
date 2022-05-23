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
MAINTAINER gngpp <verticle@foxmail.com>
LABEL name=mayi-visual-admin
LABEL url=https://github.com/gngpp/mayi
# env
ENV LANG C.UTF-8
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME
ENV JAR_FILE=./mayi-visual/mayi-visual-admin/build/libs/mayi-visual-admin-latest.jar
COPY $JAR_FILE ./target.jar
# depoly
ENV JVM_OPTS="-Xms128m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar /mayi/target.jar