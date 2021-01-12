FROM registry.access.redhat.com/ubi8/ubi:latest

RUN dnf -y update \
        && dnf -y install java-11-openjdk-headless openssl \
        && dnf -y clean all

# Set JAVA_HOME env var
ENV JAVA_HOME /usr/lib/jvm/java

COPY ./scripts/ /bin
COPY ./src/main/resources/log4j2.properties /bin/log4j2.properties

ADD target/alerting-1.0-SNAPSHOT.jar /

CMD ["/bin/run.sh", "/alerting-1.0-SNAPSHOT.jar"]