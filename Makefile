CONTAINER	:= docker

CONTAINER_TAG			:= sugu
CONTAINER_TAG_VERSION	:= latest

PHONY: docker-compose-up
docker-compose-up:
	$(CONTAINER) compose up

PHONY: docker-compose-build
docker-compose-build:
	$(CONTAINER) compose build

PHONY: docker
docker: docker-build docker-run

PHONY: docker-run
docker-run:
	$(CONTAINER) run $(CONTAINER_TAG)

PHONY: docker-build
docker-build:
	$(CONTAINER) build --tag $(CONTAINER_TAG):$(CONTAINER_TAG_VERSION) .
