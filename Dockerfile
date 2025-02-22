FROM openjdk:17-jdk-slim
EXPOSE 8099
ADD target/qp-assessment.jar qp-assessment.jar
ENTRYPOINT ["java", "-jar", "/qp-assessment.jar"]