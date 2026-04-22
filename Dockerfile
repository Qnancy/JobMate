# Base image (For Development): Eclipse Temurin JDK 21 on Ubuntu 22.04 (Jammy)
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

# Install dependencies
RUN sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/security.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/ports.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    apt-get update && apt-get install -y --no-install-recommends \
    maven \
    sudo \
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
    iproute2 \
    net-tools \
    && \
    apt-get clean && rm -rf /var/lib/apt/lists/* && \
    chsh -s /usr/bin/fish jobmate

# Change source of maven
COPY --chown=jobmate:jobmate backend/.mvn/settings.xml /home/jobmate/.m2/settings.xml

# Working directory
WORKDIR /home/jobmate/JobMate

# Default user
USER jobmate

# Default command
# CMD ["sleep", "infinity"]
CMD ["bash", "-c", "mvn spring-boot:run"]
