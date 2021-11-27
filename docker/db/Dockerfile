FROM mysql:8.0.27

MAINTAINER zf1976 <verticle@foxmail.com>
LABEL url=https://github.com/zf1976/mayi

ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD ./db/database.tar.gz ./
# master
RUN mv ./database/master.sql /docker-entrypoint-initdb.d
## nacos
RUN mv ./database/nacos.sql /docker-entrypoint-initdb.d