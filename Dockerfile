#stage1

FROM maven:3-openjdk-8 AS builder
RUN mkdir /src_folder
COPY . /src_folder
WORKDIR /src_folder
RUN mvn clean package -DskipTests


#stage2

FROM openjdk:8-jre-alpine
RUN mkdir /risk-calculator-docker
COPY --from=builder src_folder/target/dynamic-0.0.1-SNAPSHOT.jar /risk-calculator-docker
WORKDIR /risk-calculator-docker
CMD java -jar dynamic-0.0.1-SNAPSHOT.jar