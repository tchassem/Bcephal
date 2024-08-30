##################################################################################
##################################################################################
##############                                       #############################
##############  Registration of UBUNTU 22.04        #############################
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
#	&& apt-get install --assume-yes git \
	#&& echo "LC_ALL=en_US.UTF-8" >> /etc/environment \
	#&& echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen \
	#&& echo "LANG=en_US.UTF-8" > /etc/locale.conf \
	#&& locale-gen en_US.UTF-8 
	
#RUN wget https://download.java.net/java/GA/jdk18.0.2.1/db379da656dc47308e138f21b33976fa/1/GPL/openjdk-18.0.2.1_linux-x64_bin.tar.gz
#RUN ls

#RUN mkdir -p /opt/java/openjdk; \
    #tar -xf openjdk-18.0.2.1_linux-x64_bin.tar.gz -C /opt/java/openjdk && \
   # rm -rf openjdk-18.0.2.1_linux-x64_bin.tar.gz

#ENV JAVA_HOME=/opt/java/openjdk/jdk-18.0.2.1 \
   # PATH="/opt/java/openjdk/jdk-18.0.2.1/bin:$PATH"
#RUN ls /opt/java/openjdk/jdk-18.0.2.1/
#RUN ls /opt/java/openjdk/
RUN java -version


##################################################################################
##################################################################################
############                                                       ###############
############   Configuration of bcephal registration service      ###############
############                                                       ###############
##################################################################################
##################################################################################

ARG becphal_version=0.0.1
ARG becphal_port=8761
ARG becphal_jar=bcephal-registration-service-${becphal_version}.jar
ARG becphal_jars=./build/libs/${becphal_jar}
ARG becphal_server_repository=./dockerfileDataTest
ENV HOST_ADDRESS=http:\/\/localhost:8000
ARG opts="-Xms256m -Xmx512m -Dfile.encoding=UTF-8"
ARG entrypoint=./entrypoint.sh

RUN mkdir /opt/bcephal-registration-service
RUN mkdir /opt/bcephal-registration-repository
RUN mkdir /opt/bcephal-data

WORKDIR /opt/bcephal-registration-service
#WORKDIR /build/libs/bcephal-registration-service-0.0.1.jar
COPY ./build/libs/bcephal-registration-service-0.0.1.jar .
#COPY ${becphal_server_repository} /opt/repository
RUN echo 'yParam=110' > /opt/bcephal-registration-repository/security-service.properties

#RUN ls /opt/repository/


#RUN  (cd /opt/bcephal-registration-repository/ && pwd && git init && ls -l && git add . && \
#	git config --global user.email "bcephalDocker@moriset.com" && \
#	git config --global user.name "bcephalDocker" && \
#	git commit -a --allow-empty-message -m '' )


VOLUME ["/opt/bcephal-registration-repository"]
VOLUME ["/opt/bcephal-data"]

RUN echo '#!/bin/sh' > ${entrypoint}
RUN echo 'set -e' >> ${entrypoint}
RUN echo 'SOURCE_DIR=/opt/bcephal-registration-repository' >> ${entrypoint}
RUN echo 'TARGET_DIR=/opt/bcephal-registration-service' >> ${entrypoint}
RUN echo 'DATA_DIR=/opt/bcephal-data' >> ${entrypoint}
RUN echo 'rm -f  $TARGET_DIR/bootstrap.properties' >> ${entrypoint}
RUN echo 'mkdir -p $TARGET_DIR || true' >> ${entrypoint}
RUN echo 'cp   $SOURCE_DIR/bootstrap.properties $TARGET_DIR/' >> ${entrypoint}
RUN echo "sed -i -e '"'s/spring.cloud.config.uri=http:\/\/localhost:8000/spring.cloud.config.uri=${HOST_ADDRESS}/g'"' /opt/bcephal-registration-service/bootstrap.properties" >> ${entrypoint}
RUN echo '' >> ${entrypoint}
RUN echo '# continue Docker container initialization, execute CMD' >> ${entrypoint}
RUN echo 'exec $@' >> ${entrypoint}
RUN chmod ugo+rwxs  ${entrypoint}


EXPOSE ${becphal_port}


ENTRYPOINT ["./entrypoint.sh","java","-Dfile.encoding=UTF-8","-jar","/opt/bcephal-registration-service/bcephal-registration-service-0.0.1.jar"]
