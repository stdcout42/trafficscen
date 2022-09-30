FROM openjdk:11-jdk
WORKDIR /app
COPY .gradle/ .gradle
COPY gradle/ gradle  
COPY gradlew settings.gradle gradlew.bat build.gradle ./
COPY src ./src
COPY wait-for-it.sh ./
RUN chmod +x ./wait-for-it.sh
# CMD [ "./gradlew", "bootRun" ]