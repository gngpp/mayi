FROM mysql:8.0.27

MAINTAINER zf1976 <verticle@foxmail.com>
LABEL url=https://github.com/zf1976/mayi

ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD ./db/database.tar.gz /database
RUN mv ./database/db/db.sql /docker-entrypoint-initdb.d
RUN mv ./database/master.sql /docker-entrypoint-initdb.d