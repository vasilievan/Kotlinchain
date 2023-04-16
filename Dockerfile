FROM gradle:7.5.1-jdk17 as builder
USER root
WORKDIR /builder
ADD . /builder
RUN ["gradle", "jar"]

FROM openjdk:17-oracle
WORKDIR /kotlinchain
COPY --from=builder /builder/build/libs/Kotlinchain-0.0.1.jar .