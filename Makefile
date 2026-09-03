include Makefile.env

# Publishing target. Overridable from Makefile.env or the environment.
REGISTRY ?= registry.lunareclipse.ch

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
push: build docker-push

ifneq ($(VERSION),latest)
LATEST_TAG := -t $(REGISTRY)/$(CONTAINER_TAG):latest
endif

# buildx pushes straight out of BuildKit, which registers every child
# manifest. A plain `docker push` under the containerd image store uploads
# them as blobs only, leaving a tagged index the registry cannot resolve.
.PHONY: docker-push
docker-push:
	@echo "Pushing $(REGISTRY)/$(CONTAINER_TAG_NAME)"
	docker buildx build --platform linux/amd64 \
		-t $(REGISTRY)/$(CONTAINER_TAG_NAME) $(LATEST_TAG) \
		--push .
