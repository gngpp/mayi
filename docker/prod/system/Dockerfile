# Debian --- OpenJ9-Jlink
FROM openjdk:17-alpine as jre-build
USER root
WORKDIR /mayi
# Cache dependencies
COPY ./gradle ./gradle
COPY ./settings.gradle ./settings.gradle
COPY ./gradle.properties ./gradle.properties
COPY ./gradlew ./gradlew
COPY ./gradlew.bat ./gradlew.bat
COPY ./lombok.config ./lombok.config
COPY ./build.gradle ./build.gradle
COPY ./mayi-auth/build.gradle ./mayi-auth/build.gradle
COPY ./mayi-common/mayi-common-component/build.gradle ./mayi-common/mayi-common-component/build.gradle
COPY ./mayi-common/mayi-common-core/build.gradle ./mayi-common/mayi-common-core/build.gradle
COPY ./mayi-common/mayi-common-datasource/build.gradle ./mayi-common/mayi-common-datasource/build.gradle
COPY ./mayi-common/mayi-common-encrypt/build.gradle ./mayi-common/mayi-common-encrypt/build.gradle
COPY ./mayi-common/mayi-common-remote/build.gradle ./mayi-common/mayi-common-remote/build.gradle
COPY ./mayi-common/mayi-common-security/build.gradle ./mayi-common/mayi-common-security/build.gradle
COPY ./mayi-gateway/build.gradle ./mayi-gateway/build.gradle
COPY ./mayi-upms/mayi-upms-api/build.gradle ./mayi-upms/mayi-upms-api/build.gradle
COPY ./mayi-upms/mayi-upms-biz/build.gradle ./mayi-upms/mayi-upms-biz/build.gradle
COPY ./mayi-visual/mayi-visual-admin/build.gradle ./mayi-visual/mayi-visual-admin/build.gradle
RUN chmod +x ./gradlew
RUN ./gradlew resolveDependencies --scan --info
COPY ./mayi-auth ./mayi-auth
COPY ./mayi-common ./mayi-common
COPY ./mayi-gateway ./mayi-gateway
COPY ./mayi-upms ./mayi-upms
COPY ./mayi-visual ./mayi-visual
RUN ./gradlew -b ./mayi-upms/mayi-upms-biz/build.gradle bootJar --info
# Cache config
COPY ./bootstrap.yml ./bootstrap.yml
# Rename mayi-upms-biz Jar
RUN mv ./mayi-upms/mayi-upms-biz/build/libs/mayi-upms-biz-latest.jar ./target.jar
# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules ALL-MODULE-PATH \
         --output /javaruntime


FROM alpine
USER root
WORKDIR /mayi
MAINTAINER zf1976 <verticle@foxmail.com>
LABEL name=mayi-upms-biz
LABEL url=https://github.com/zf1976/mayi

RUN apk add curl
# env
ENV LANG C.UTF-8
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
ENV JAR_FILE=/mayi/target.jar
ENV CONFIG=./bootstrap.yml
ENV RUN_SCRIPT=./run
# copy
COPY --from=jre-build /javaruntime $JAVA_HOME
COPY --from=jre-build $JAR_FILE ./target.jar
COPY $CONFIG ./bootstrap.yml
COPY $RUN_SCRIPT ./run
# deploy
ENV JVM_OPTS="-Xms256m -Xmx256m" \
    TZ=Asia/Shanghai
RUN chmod +x ./run
CMD ["sh","./run"]