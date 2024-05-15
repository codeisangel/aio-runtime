FROM openjdk:8-jre-alpine
# 作者
MAINTAINER zhaokejin

ENV EXTERNAL_CONFIG=""
ENV TZ Asia/Shanghai
ENV BOOT_OPTIONS=""
ENV BOOT_PROFILES = ""

VOLUME /config

ADD target/guodun-document-*.jar guodun-document.jar
# 解决时间差
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime
RUN echo $TZ > /etc/timezone

ENTRYPOINT ["sh","-c","java -jar /guodun-document.jar ${BOOT_OPTIONS} ${BOOT_PROFILES} ${EXTERNAL_CONFIG}"]
