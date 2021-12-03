#!/bin/sh
#
# /*
#  * Copyright (c) 2021 zf1976
#  *
#  * Permission is hereby granted, free of charge, to any person obtaining a copy
#  * of this software and associated documentation files (the "Software"), to deal
#  * in the Software without restriction, including without limitation the rights
#  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#  * copies of the Software, and to permit persons to whom the Software is
#  * furnished to do so, subject to the following conditions:
#  *
#  * The above copyright notice and this permission notice shall be included in all
#  * copies or substantial portions of the Software.
#  *
#  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
#  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
#  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
#  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
#  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
#  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
#  * SOFTWARE.
#  *
#  */
#

#./gradlew clean && ./gradlew bootJar
pp=$(pwd)
export MAYI_AUTH=$pp//mayi-auth/build/libs/mayi-auth-latest.jar
export MAYI_UPMS_BIZ=$pp/mayi-upms/mayi-upms-biz/build/libs/mayi-upms-biz-latest.jar
export MAYI_GATEWAY=$pp/mayi-gateway/build/libs/mayi-gateway-latest.jar
export MAYI_VISUAL_ADMIN=$pp/mayi-visual-admin/build/libs/mayi-visual-admin-latest.jar
export CONFIG=$pp/bootstrap.yml

export MAYI_AUTH_port=9000
export MAYI_UPMS_BIZ_port=7777
export MAYI_GATEWAY_port=8888
export MAYI_VISUAL_ADMIN_port=4444
read -p "Please enter the Y/N:" yesNo
case $yesNo in
[yY])
        ## start mayi-auth
        echo "REGISTER开始启动----------------------"
        nohub java -Xms256m -Xmx256m -jar $MAYI_AUTH --spring.config.additional-location=$CONFIG 1>>./logs/out.log 2>&1 &
        MAYI_AUTH_pid=`lsof -i:$MAYI_AUTH_port|grep "LISTEN"|awk '{print $2}'`
        until [ -n "$MAYI_AUTH_pid" ]
            do
              MAYI_AUTH_pid=`lsof -i:$MAYI_AUTH_port|grep "LISTEN"|awk '{print $2}'`
            done
        echo "----------------------mayi-auth 启动成功pid:$MAYI_AUTH_pid"

		    ## start mayi-gateway
		    echo "GATEWAY开始启动-----------------------"
		    nohub java -Xms256m -Xmx256m -jar $MAYI_GATEWAY --spring.config.additional-location=$CONFIG 1>>./logs/out.log 2>&1 &
		    GATEWAY_pid=`lsof -i:$MAYI_GATEWAY_port|grep "LISTEN"|awk '{print $2}'`
		    until [ -n "$GATEWAY_pid" ]
			    do
			      GATEWAY_pid=`lsof -i:$MAYI_GATEWAY_port|grep "LISTEN"|awk '{print $2}'`
			    done
		    echo "-----------------------mayi-gateway 启动成功pid:$GATEWAY_pid"


        ## start mayi-visual-admin
        echo "AUTH开始启动--------------------------"
        nohub java -Xms256m -Xmx256m -jar $MAYI_VISUAL_ADMIN 1>>./logs/out.log 2>&1 &
        MAYI_VISUAL_ADMIN_pid=`lsof -i:$MAYI_VISUAL_ADMIN_port|grep "LISTEN"|awk '{print $2}'`
        until [ -n "$MAYI_VISUAL_ADMIN_pid" ]
            do
              MAYI_VISUAL_ADMIN_pid=`lsof -i:$MAYI_VISUAL_ADMIN_port|grep "LISTEN"|awk '{print $2}'`
            done
        echo "--------------------------mayi-visual-admin 启动成功pid:$MAYI_VISUAL_ADMIN_pid"

        ## start mayi-upms-biz
        echo "MAYI_UPMS_BIZ开始启动--------------------------"
        nohub java -Xms256m -Xmx256m -jar $MAYI_UPMS_BIZ --spring.config.additional-location=$CONFIG 1>>./logs/out.log 2>&1 &
        MAYI_UPMS_BIZ_pid=`lsof -i:$MAYI_UPMS_BIZ_port|grep "LISTEN"|awk '{print $2}'`
        until [ -n "$MAYI_UPMS_BIZ_pid" ]
            do
              MAYI_UPMS_BIZ_pid=`lsof -i:$MAYI_UPMS_BIZ_port|grep "LISTEN"|awk '{print $2}'`
            done
        echo "--------------------------mayi-upms-biz 启动成功pid:$MAYI_UPMS_BIZ_pid"
        echo "===startAll success==="
        ;;

[nN])
        P_ID=`ps -ef | grep -w $MAYI_AUTh | grep -v "grep" | awk '{print $2}'`
        if [ "$P_ID" == "" ]; then
            echo "===MAYI_AUTH process not exists or stop success"
        else
            kill -9 $P_ID
            echo "MAYI_AUTH killed success"
        fi
		P_ID=`ps -ef | grep -w $MAYI_UPMS_BIZ | grep -v "grep" | awk '{print $2}'`
        if [ "$P_ID" == "" ]; then
            echo "===MAYI_UPMS_BIZ process not exists or stop success"
        else
            kill -9 $P_ID
            echo "MAYI_UPMS_BIZ killed success"
        fi
		 P_ID=`ps -ef | grep -w $MAYI_GATEWAY | grep -v "grep" | awk '{print $2}'`
        if [ "$P_ID" == "" ]; then
            echo "===MAYI_GATEWAY process not exists or stop success"
        else
            kill -9 $P_ID
            echo "MAYI_GATEWAY killed success"
        fi
		 P_ID=`ps -ef | grep -w $MAYI_VISUAL_ADMIN | grep -v "grep" | awk '{print $2}'`
        if [ "$P_ID" == "" ]; then
            echo "===MAYI_VISUAL_ADMIN process not exists or stop success"
        else
            kill -9 $P_ID
            echo "MAYI_VISUAL_ADMIN killed success"
        fi

        echo "===stop success==="
        ;;

restart)
        $0 stop
        sleep 2
        $0 start
        echo "===restart success==="
        ;;
esac
exit 0