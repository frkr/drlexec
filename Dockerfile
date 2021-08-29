FROM openjdk:16
EXPOSE 80
VOLUME /opt/drlexec/config
VOLUME /opt/drlexec/log
WORKDIR /opt/drlexec

COPY target/drlexec.jar /opt/drlexec
#COPY config /opt/drlexec/config

CMD java -Djava.security.egd=file:/dev/./urandom -Xss2m -jar drlexec.jar
