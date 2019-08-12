FROM ubuntu
RUN apt-get update
RUN apt-get install -y time
RUN apt-get install -y binutils
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y python3

