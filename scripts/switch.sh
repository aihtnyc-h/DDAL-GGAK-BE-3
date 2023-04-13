# #!/bin/bash
# echo "> 현재 구동중인 Port 확인"
# CURRENT_PROFILE=$(curl -s ${{ secrets.INSTANCE_URL }}/profile)

# if [ $CURRENT_PROFILE == set1 ]
# then
#  IDLE_PORT=8082
# elif [ $CURRENT_PROFILE == set2 ]
# then
#  IDLE_PORT=8081
# else
#  echo "> 일치하는 Profile이 없습니다. Profile:$CURRENT_PROFILE"
#  echo "> 8081을 할당합니다."
#  IDLE_PORT=8081
# fi

# PROXY_PORT=$(curl -s ${{ secrets.INSTANCE_URL }}/profile)
# echo "> 현재 구동중인 Port: $PROXY_PORT"

# echo "> 전환할 Port : $IDLE_PORT"
# #DDAL-GGAK-BE-0.0.1-SNAPSHOT.jar  
# # 현재 실행 중인 DDAL-GGAK-BE-0.0.1-SNAPSHOT.jar이 있다면 프로세스를 종료합니다.
# echo "> 실행 중인 DDAL-GGAK-BE-0.0.1-SNAPSHOT.jar 프로세스 확인"
# CURRENT_PID=$(sudo lsof -t -i:${PROXY_PORT})
# if [ -z "$CURRENT_PID" ]
# then
#  echo "> 현재 구동 중인 애플리케이션이 없습니다."
# else
#  echo "> 현재 구동 중인 애플리케이션의 PID: $CURRENT_PID"
#  echo "> DDAL-GGAK-BE-0.0.1-SNAPSHOT.jar 종료"
#  sudo kill -15 $CURRENT_PID
#  sleep 5
# fi


# # 새로운 포트로 DDAL-GGAK-BE-server-0.0.1-SNAPSHOT.jar를 실행시킵니다.
# echo "> $IDLE_PORT 포트로 DDAL-GGAK-BE-0.0.1-SNAPSHOT.jar 실행"
# sudo nohup java -jar /home/ubuntu/app/DDAL-GGAK-BE-0.0.1-SNAPSHOT.jar --spring.config.location=file:/home/ubuntu/app/config/prod-application.yaml --spring.profiles.active=$CURRENT_PROFILE --server.port=$IDLE_PORT &

# # Nginx 설정을 변경하여 새로운 포트로 전환합니다.
# echo "> Nginx 설정 변경"
# echo "set \$service_url ${{ secrets.INSTANCE_URL }}:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc
# echo "> Nginx Reload"
# sudo service nginx reload
