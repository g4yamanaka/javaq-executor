FROM docker:stable-dind
RUN apk update \
    && apk upgrade \
    && apk add coreutils curl


ARG BUILD_DATE
ARG VCS_REF

# Set Appropriate Environmental Variables
ENV GRADLE_HOME /usr/lib/gradle
ENV GRADLE_VERSION 5.5
ENV GRADLE_FOLDER=/root/.gradle

ARG GRADLE_DOWNLOAD_SHA256=8d78b2ed63e7f07ad169c1186d119761c4773e681f332cfe1901045b1b0141bc
RUN set -o errexit -o nounset \
    && echo "Installing build dependencies" \
    && apk add --no-cache --virtual .build-deps \
        ca-certificates \
        openssl \
        unzip \
    \
    && echo "Downloading openjdk11" \
    && apk add --no-cache openjdk11 \
    && echo "Downloading Gradle" \
    && wget -O gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    \
    && echo "Checking download hash" \
    && echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum -c - \
    \
    && echo "Installing Gradle" \
    && unzip gradle.zip \
    && rm gradle.zip \
    && mkdir -p /opt \
    && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
    && ln -s "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
    \
    && apk del .build-deps \
    \
    && echo "Adding gradle user and group" \
    && addgroup -S -g 1000 gradle \
    && adduser -D -S -G gradle -u 1000 -s /bin/ash gradle \
    && mkdir /home/gradle/.gradle \
    && chown -R gradle:gradle /home/gradle \
    \
    && echo "Symlinking root Gradle cache to gradle Gradle cache" \
    && ln -s /home/gradle/.gradle /root/.gradle

LABEL org.label-schema.build-date=$BUILD_DATE \
      org.label-schema.description="Kotlin docker images built upon official openjdk alpine images" \
      org.label-schema.name="alpine-kotlin" \
      org.label-schema.schema-version="1.0.0-rc1" \
      org.label-schema.usage="https://github.com/Zenika/alpine-kotlin/blob/master/README.md" \
      org.label-schema.vcs-url="https://github.com/Zenika/alpine-kotlin" \
      org.label-schema.vcs-ref=$VCS_REF \
      org.label-schema.vendor="Zenika" \
      org.label-schema.version="1.2-jdk8"

RUN apk add --no-cache bash && \
    apk add --no-cache -t build-dependencies wget && \
    cd /usr/lib && \
    wget https://github.com/JetBrains/kotlin/releases/download/v1.3.30/kotlin-compiler-1.3.30.zip && \
    unzip kotlin-compiler-*.zip && \
    rm kotlin-compiler-*.zip && \
    rm kotlinc/bin/*.bat && \
    apk del --no-cache build-dependencies

ENV PATH $PATH:/usr/lib/kotlinc/bin


RUN apk del coreutils curl

WORKDIR /app
ADD ./gradlew /app
ADD ./src /app/src
ADD ./build.gradle.kts /app
ADD ./settings.gradle.kts /app
ADD ./gradle /app/gradle
ENTRYPOINT ["sh", "./gradlew", "bootRun"]

VOLUME  $GRADLE_FOLDER