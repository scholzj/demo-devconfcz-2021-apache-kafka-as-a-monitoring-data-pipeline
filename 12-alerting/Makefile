PROJECT_NAME=kafka-streams-alerting

DOCKERFILE_DIR     ?= ./
DOCKER_REGISTRY    ?= quay.io
DOCKER_ORG         ?= $(USER)
DOCKER_TAG         ?= latest
DOCKER_VERSION_ARG ?= latest

all: java_build docker_build docker_push
build: java_build
clean: java_clean

docker_build:
	echo "Building Docker image ..."
	docker build --squash -t strimzi/$(PROJECT_NAME):$(DOCKER_TAG) $(DOCKERFILE_DIR)

docker_tag:
	echo "Tagging strimzi/$(PROJECT_NAME):$(DOCKER_TAG) to $(DOCKER_REGISTRY)/$(DOCKER_ORG)/$(PROJECT_NAME):$(DOCKER_TAG) ..."
	docker tag strimzi/$(PROJECT_NAME):$(DOCKER_TAG) $(DOCKER_REGISTRY)/$(DOCKER_ORG)/$(PROJECT_NAME):$(DOCKER_TAG)

docker_push: docker_tag
	echo "Pushing $(DOCKER_REGISTRY)/$(DOCKER_ORG)/$(PROJECT_NAME):$(DOCKER_TAG) ..."
	docker push $(DOCKER_REGISTRY)/$(DOCKER_ORG)/$(PROJECT_NAME):$(DOCKER_TAG)

java_build:
	echo "Building JAR file ..."
	mvn package

java_clean:
	echo "Cleaning Maven build ..."
	mvn clean

.PHONY: build clean