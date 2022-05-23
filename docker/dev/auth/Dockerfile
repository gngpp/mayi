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
LABEL name=mayi-auth
LABEL url=https://github.com/gngpp/mayi

RUN apk add curl
# env
ENV LANG C.UTF-8
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
ENV JAR_FILE=./mayi-auth/build/libs/mayi-auth-latest.jar
ENV CONFIG=./bootstrap.yml
ENV RUN_SCRIPT=./run
# copy
COPY --from=jre-build /javaruntime $JAVA_HOME
COPY $JAR_FILE ./target.jar
COPY $CONFIG ./bootstrap.yml
COPY $RUN_SCRIPT ./run
# depoly
ENV JVM_OPTS="-Xms128m -Xmx256m" \
    TZ=Asia/Shanghai
RUN chmod +x ./run
CMD ["sh","./run"]