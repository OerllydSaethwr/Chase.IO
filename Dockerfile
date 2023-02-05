FROM eclipse-temurin:17
ADD target/demo-1.0-SNAPSHOT-jar-with-dependencies.jar demo-1.0-SNAPSHOT-jar-with-dependencies.jar
EXPOSE 4567
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "demo-1.0-SNAPSHOT-jar-with-dependencies.jar"]