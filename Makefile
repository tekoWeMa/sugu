include Makefile.env

CONTAINER			:= docker
CONTAINER_COMPOSE	:= $(CONTAINER) compose

CONTAINER_TAG			:= sugu
CONTAINER_TAG_VERSION	:= latest
CONTAINER_TAG_NAME		:= $(CONTAINER_TAG):$(CONTAINER_TAG_VERSION)

PHONY: build
build:	docker-compose-build

PHONY: stop
stop:	docker-compose-stop

PHONY: up
up:	docker-compose-up

PHONY: docker-compose-build
docker-compose-build:
	$(CONTAINER_COMPOSE) build

PHONY: docker-compose-stop
docker-compose-stop:
	$(CONTAINER_COMPOSE) stop

PHONY: docker-compose-up
docker-compose-up:
	$(CONTAINER_COMPOSE) up --detach