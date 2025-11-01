# Base image: Eclipse Temurin JDK 21 on Ubuntu 22.04 (Jammy)
FROM eclipse-temurin:21-jdk-jammy

# Create group and user
RUN groupadd -g 1000 jobmate && \
    useradd -u 1000 -g jobmate -d /home/jobmate -m jobmate && \
    usermod -aG sudo jobmate && \
    mkdir -p /etc/sudoers.d && \
    echo "jobmate ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/jobmate && \
    chmod 0440 /etc/sudoers.d/jobmate && \
    mkdir -p /home/jobmate/JobMate && \
    chown -R jobmate:jobmate /home/jobmate

# Replace the default apt source with Aliyun source
RUN echo "deb http://mirrors.aliyun.com/ubuntu/ jammy main restricted universe multiverse" > /etc/apt/sources.list && \
    echo "deb http://mirrors.aliyun.com/ubuntu/ jammy-updates main restricted universe multiverse" >> /etc/apt/sources.list && \
    echo "deb http://mirrors.aliyun.com/ubuntu/ jammy-backports main restricted universe multiverse" >> /etc/apt/sources.list && \
    echo "deb http://mirrors.aliyun.com/ubuntu/ jammy-security main restricted universe multiverse" >> /etc/apt/sources.list

# Install dependencies
RUN apt-get update && \
    apt-get install -y \
    maven \
    fish \
    mysql-client \
    redis-tools \
    curl \
    wget \
    git \
    vim \
    zip \
    unzip \
    lsof \
    sudo \
    iproute2 \
    net-tools \
    && \
    rm -rf /var/lib/apt/lists/* && \
    chsh -s /usr/bin/fish jobmate

# Change source of maven
RUN mkdir -p /home/jobmate/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>\
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">\
  <mirrors>\
    <mirror>\
      <id>aliyun</id>\
      <name>Aliyun Maven Repository</name>\
      <url>https://maven.aliyun.com/repository/public</url>\
      <mirrorOf>central</mirrorOf>\
    </mirror>\
  </mirrors>\
</settings>' > /home/jobmate/.m2/settings.xml && \
    chown -R jobmate:jobmate /home/jobmate/.m2

# Working directory
WORKDIR /home/jobmate/JobMate

# Default user
USER jobmate

# Default command
CMD ["sleep", "infinity"]
