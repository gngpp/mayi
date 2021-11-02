# Debian --- OpenJ9-Jlink
FROM adoptopenjdk:11-openj9 as jre-build
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
COPY ./mayi-common/mayi-common-log/build.gradle ./mayi-common/mayi-common-log/build.gradle
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
RUN ./gradlew -b ./mayi-visual/mayi-visual-admin/build.gradle bootJar --info
# Rename mayi-admin-monitor Jar
RUN mv ./mayi-visual/mayi-visual-admin/build/libs/mayi-visual-admin-latest.jar ./target.jar

FROM adoptopenjdk:11-jre-openj9
USER root
WORKDIR /mayi
MAINTAINER zf1976 <verticle@foxmail.com>
LABEL name=mayi-admin-monitor
LABEL url=https://github.com/zf1976/mayi
# env
ENV LANG C.UTF-8
ENV JAR_FILE=/mayi/target.jar
ENV CONFIG=./bootstrap.yml
COPY --from=jre-build $JAR_FILE ./target.jar
COPY $CONFIG ./bootstrap.yml
# depoly
ENV JVM_OPTS="-Xms256m -Xmx256m" \
    TZ=Asia/Shanghai
CMD exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar /mayi/target.jar