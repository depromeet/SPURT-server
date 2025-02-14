FROM openjdk:17-jdk-alpine

WORKDIR /app

# Gradle 빌드 후 생성된 jar 파일을 컨테이너로 복사
# JAR_FILE 변수에 실제 jar 파일 경로를 지정
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Duser.timezone=Asia/Seoul", "-jar", "/app/app.jar"]