##################################################################################
##################################################################################
##############                                       #############################
##############  Configuration of UBUNTU 22.04        #############################
##############                                       #############################
##################################################################################
##################################################################################

FROM openjdk:18

ARG DEBIAN_FRONTEND=noninteractive

#RUN apt-get update &&  apt-get install -y --no-install-recommends apt-utils

#RUN apt-get install -y --no-install-recommends locales locales-all

ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8

#RUN apt-get install -y --no-install-recommends apt-utils tzdata net-tools wget curl unzip ca-certificates fontconfig \
	#&& apt-get install --assume-yes git \
	#&& echo "LC_ALL=en_US.UTF-8" >> /etc/environment \
	#&& echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen \
	#&& echo "LANG=en_US.UTF-8" > /etc/locale.conf \
	#&& locale-gen en_US.UTF-8 
RUN microdnf install git	
	
#RUN wget https://download.java.net/java/GA/jdk18.0.2.1/db379da656dc47308e138f21b33976fa/1/GPL/openjdk-18.0.2.1_linux-x64_bin.tar.gz
#RUN ls

#RUN mkdir -p /opt/java/openjdk; \
#    tar -xf openjdk-18.0.2.1_linux-x64_bin.tar.gz -C /opt/java/openjdk && \
 #   rm -rf openjdk-18.0.2.1_linux-x64_bin.tar.gz

#ENV JAVA_HOME=/opt/java/openjdk/jdk-18.0.2.1 \
 #   PATH="/opt/java/openjdk/jdk-18.0.2.1/bin:$PATH"
#RUN ls /opt/java/openjdk/jdk-18.0.2.1/
#RUN ls /opt/java/openjdk/
RUN java -version


##################################################################################
##################################################################################
############                                                       ###############
############   Configuration of bcephal configuration service      ###############
############                                                       ###############
##################################################################################
##################################################################################

ARG becphal_version=0.0.1
ARG becphal_port=8000
ARG becphal_jar=bcephal-configuration-service-${becphal_version}.jar
ARG becphal_jars=./build/libs/${becphal_jar}
ARG becphal_server_repository=./dockerfileDataTest

ARG opts="-Xms256m -Xmx512m -Dfile.encoding=UTF-8"

ARG entrypoint=./entrypoint.sh

RUN mkdir /opt/bcephal-configuration-service
RUN mkdir /opt/bcephal-configuration-repository

WORKDIR /opt/bcephal-configuration-service
#WORKDIR /build/libs/bcephal-configuration-service-0.0.1.jar
COPY ./build/libs/bcephal-configuration-service-0.0.1.jar .
COPY ./build/resources/main/application.properties .

COPY  ./bcephal-configuration-repository /opt/repository
RUN echo 'yParam=110' > /opt/bcephal-configuration-repository/security-service.properties

RUN ls /opt/repository/


RUN  (cd /opt/bcephal-configuration-repository/ && pwd && git init && ls -l && git add . && \
	git config --global user.email "bcephalDocker@moriset.com" && \
	git config --global user.name "bcephalDocker" && \
	git commit -a --allow-empty-message -m '' )


VOLUME ["/opt/bcephal-configuration-repository"]


RUN echo '#!/bin/sh' > ${entrypoint}
RUN echo 'set -e' >> ${entrypoint}
RUN echo 'SOURCE_DIR=/opt/repository' >> ${entrypoint}
RUN echo 'TARGET_DIR=/opt/bcephal-configuration-repository' >> ${entrypoint}
#RUN echo 'if [ $(find $TARGET_DIR -maxdepth 0 -type d -empty) 2>/dev/null) ]; then' >> ${entrypoint}
#RUN echo 'cp -r --preserve-all $SOURCE_DIR/* $TARGET_DIR/' >> ${entrypoint}
RUN echo 'mkdir -p $TARGET_DIR || true' >> ${entrypoint}
RUN echo 'cp -rn  $SOURCE_DIR/* $TARGET_DIR/' >> ${entrypoint}
RUN echo "sed -i -e '"'s/spring.cloud.config.server.git.uri=\/\/${user.dir}\/../spring.cloud.config.server.git.uri=\/opt/g'"' /opt/bcephal-configuration-service/application.properties" >> ${entrypoint}
RUN echo "sed -i -e '"'s/logging.file.name=\/\/${user.home}\//logging.file.name=\/opt\/bcephal-data\//g'"' /opt/bcephal-configuration-service/application.properties" >> ${entrypoint}
RUN echo 'cp   /opt/bcephal-configuration-service/application.properties $TARGET_DIR/application0.properties' >> ${entrypoint}
RUN echo 'ls  /opt/bcephal-configuration-service > $TARGET_DIR/application1.properties' >> ${entrypoint}
RUN echo 'ls  /opt > $TARGET_DIR/application2.properties' >> ${entrypoint}
RUN echo 'ls  /opt/bcephal-configuration-repository > $TARGET_DIR/application3.properties' >> ${entrypoint}
RUN echo "sed -i -e '"'s/eureka.client.serviceUrl.defaultZone=${eureka-protocol}:\/\/${eureka.instance.hostname}:${eureka.server.port}\/eureka\//eureka.client.serviceUrl.defaultZone=${eureka-protocol}:\/\/${eureka.instance.hostname}.eureka:${eureka.server.port}\/eureka\//g'"' /opt/bcephal-configuration-service/application.properties" >> ${entrypoint}
RUN echo "sed -i -e '"'s/eureka.instance.hostname=192.168.100.114/eureka.instance.hostname=eureka-0/g'"' /opt/bcephal-configuration-service/application.properties" >> ${entrypoint}
RUN echo "sed -i -e '"'s/eureka.instance.hostname=192.168.100.55/eureka.instance.hostname=eureka-0/g'"' /opt/bcephal-configuration-service/application.properties" >> ${entrypoint}
RUN echo "sed -i -e '"'s/#eureka.instance.preferIpAddress=true/eureka.instance.preferIpAddress=true/g'"' /opt/bcephal-configuration-service/application.properties" >> ${entrypoint}

RUN echo "sed -i -e '"'s/#eureka.client.serviceUrl.defaultZone=${EUREKA_SERVER_ADDRESS}/eureka.client.serviceUrl.defaultZone=${EUREKA_SERVER_ADDRESS}/g'"' /opt/bcephal-configuration-service/application.properties" >> ${entrypoint}
RUN echo "sed -i -e '"'s/#eureka.instance.hostname="${HOSTNAME}.eureka"/eureka.instance.hostname="${HOSTNAME}.eureka"/g'"' /opt/bcephal-configuration-service/application.properties" >> ${entrypoint}


#RUN echo 'fi' >> ${entrypoint}
RUN echo '' >> ${entrypoint}
RUN echo '(cd /opt/bcephal-configuration-repository/ && pwd && git init && ls -l && git add . && \' >> ${entrypoint}
RUN echo 'git config --global user.email "bcephalDocker@moriset.com" && \' >> ${entrypoint}
RUN echo 'git config --global user.name "bcephalDocker" && \' >> ${entrypoint}
RUN echo 'git commit -a --allow-empty-message -m "u" )|| true' >> ${entrypoint}
#RUN echo '(cd /opt/bcephal-configuration-repository/ && git commit -a --allow-empty-message -m "u")' >> ${entrypoint}
RUN echo '# continue Docker container initialization, execute CMD' >> ${entrypoint}
RUN echo 'exec $@' >> ${entrypoint}
RUN chmod ugo+rwxs  ${entrypoint}


EXPOSE ${becphal_port}


ENTRYPOINT ["./entrypoint.sh","java","-Dfile.encoding=UTF-8","-jar","/opt/bcephal-configuration-service/bcephal-configuration-service-0.0.1.jar"]
