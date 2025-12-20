include Makefile.env

CONTAINER			:= docker
CONTAINER_COMPOSE	:= $(CONTAINER) compose

CONTAINER_TAG			:= sugu
VERSION					?= latest
CONTAINER_TAG_NAME		:= $(CONTAINER_TAG):$(VERSION)

PHONY: build
build: docker-compose-build

PHONY: down
down: stop

PHONY: stop
stop: docker-compose-stop

PHONY: up
up: docker-compose-up

PHONY: docker-compose-build
docker-compose-build:
	$(CONTAINER_COMPOSE) build

PHONY: docker-compose-stop
docker-compose-stop:
	$(CONTAINER_COMPOSE) stop

PHONY: docker-compose-up
docker-compose-up:
	$(CONTAINER_COMPOSE) up --detach

.PHONY: push
push: build docker-tag docker-push

.PHONY: docker-tag
docker-tag:
	@echo "Tagging image as $(DOCKERHUB_USER)/$(CONTAINER_TAG_NAME)"
	docker tag $(CONTAINER_TAG_NAME) $(DOCKERHUB_USER)/$(CONTAINER_TAG_NAME)
ifneq ($(VERSION),latest)
	@echo "Tagging image as $(DOCKERHUB_USER)/$(CONTAINER_TAG):latest"
	docker tag $(CONTAINER_TAG_NAME) $(DOCKERHUB_USER)/$(CONTAINER_TAG):latest
endif

.PHONY: docker-push
docker-push:
	@echo "Pushing $(DOCKERHUB_USER)/$(CONTAINER_TAG_NAME) to Docker Hub"
	docker push $(DOCKERHUB_USER)/$(CONTAINER_TAG_NAME)
ifneq ($(VERSION),latest)
	@echo "Pushing $(DOCKERHUB_USER)/$(CONTAINER_TAG):latest to Docker Hub"
	docker push $(DOCKERHUB_USER)/$(CONTAINER_TAG):latest
endif