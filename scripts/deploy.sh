# #!/bin/bash

# REPOSITORY=/home/ec2-user/app/step2
# PROJECT_NAME=DDAL-GGAK

# echo "> Build 파일 복사"

# cp $REPOSITORY/zip/*.jar $REPOSITORY/

# echo "> 현재 구동중인 애플리케이션 pid 확인"

# # 수행 중인 애플리케이션 프로세스 ID => 구동 중이면 종료하기 위함
# CURRENT_PID=$(pgrep -fl $PROJECT_NAME | grep jar | awk '{print $1}')

# echo "현재 구동중인 어플리케이션 pid: $CURRENT_PID"

# if [ -z "$CURRENT_PID" ]; then
#     echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
# else
#     echo "> kill -15 $CURRENT_PID"
#     kill -15 $CURRENT_PID
#     sleep 5
# fi

# echo "> 새 어플리케이션 배포"

# JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

# echo "> JAR Name: $JAR_NAME"

# echo "> $JAR_NAME 에 실행권한 추가"

# chmod +x $JAR_NAME # Jar 파일은 실행 권한이 없는 상태이므로 권한 부여

# echo "> $JAR_NAME 실행"

# nohup java -jar \
#     -Dspring.config.location=classpath:/application.yaml,classpath:/application.yaml,/home/ubuntu/app/config/prod-application-set1.yaml,/home/ubuntu/app/config/prod-application-set2.yaml \
#     -Dspring.profiles.active=real \
#     $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
# # nohup 실행 시 CodeDeploy는 무한 대기한다. 이를 해결하기 위해 nohup.out 파일을 표준 입출력용으로 별도로 사용한다.
# # 이렇게 하지 않으면 nohup.out 파일이 생성되지 않고 CodeDeploy 로그에 표준 입출력이 출력된다.


BUILD_PATH=$(ls $BASE_PATH/build/build/libs/*.jar)
JAR_NAME=$(ls $BUILD_PATH | grep 'testcicd' | tail -n 1 | xargs -0 -n 1 basename )
echo "> build 파일명: $JAR_NAME"

echo "> build 파일 복사"
DEPLOY_PATH=/home/ubuntu/app/nginx/jar/
sudo cp $BUILD_PATH $DEPLOY_PATH


RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/nginx)

echo ">$RESPONSE_CODE response code"

# 아무것도 구동중이지  않을 때 우리가 프로젝트에서 만든 api로 조회하면 당연히 오류가 난다. 이를 방지!
if [ ${RESPONSE_CODE} -ge 400 ]
then
        echo "> There is no running app lets set s2"
        CURRENT_PROFILE=s2
else
        CURRENT_PROFILE=$(curl -s http://localhost/nginx)
fi


echo ">$CURRENT_PROFILE current profile"

if [ $CURRENT_PROFILE == s1 ]
then
  IDLE_PROFILE=s2
  IDLE_PORT=8082
elif [ $CURRENT_PROFILE == s2 ]
then
  IDLE_PROFILE=s1
  IDLE_PORT=8081
else
  echo "> 일치하는 Profile이 없습니다. Profile: $CURRENT_PROFILE"
  echo "> s1을 할당합니다. IDLE_PROFILE: s1"
  IDLE_PROFILE=s1
  IDLE_PORT=8081
fi

IDLE_APPLICATION=$IDLE_PROFILE-testcicd.jar

sudo ln -Tfs $DEPLOY_PATH$JAR_NAME $DEPLOY_PATH$IDLE_PROFILE-testcicd.jar


echo "> $IDLE_PROFILE 에서 구동중인 애플리케이션 pid 확인"
IDLE_PID=$(pgrep -f $IDLE_APPLICATION)

if [ -z $IDLE_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $IDLE_PID"
  sudo kill -15 $IDLE_PID
  sleep 10
fi

echo "> $IDLE_PROFILE 배포"
echo "> Change Directory to $DEPLOY_PATH "
cd $DEPLOY_PATH
echo "> $IDLE_APPLICATION Deploying "

nohup java -jar $IDLE_APPLICATION --spring.ymal.default=$IDLE_PROFILE &
