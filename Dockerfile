FROM bitnami/java:17 as builder
RUN apt-get update -y && apt-get install maven -y
COPY . .
RUN mvn clean package -D maven.test.skip=true


FROM bitnami/java:17
ENV APPLICATION_DIR="/target/"
ENV MODULE="Analytics-0.0.1-SNAPSHOT.jar"

COPY --from=builder $APPLICATION_DIR/$MODULE $APPLICATION_DIR/$MODULE
CMD java -jar $APPLICATION_DIR/$MODULE
expose 8083