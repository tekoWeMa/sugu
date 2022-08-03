CONTAINER			:= docker
CONTAINER_COMPOSE	:= "$(CONTAINER) compose"

CONTAINER_TAG			:= sugu
CONTAINER_TAG_VERSION	:= latest

PHONY: update
update: docker-compose-stop
	git checkout .
	git pull

PHONY: docker-compose-up
docker-compose-up:
	$(CONTAINER) compose up -d

PHONY: docker-compose-build
docker-compose-build:
	$(CONTAINER) compose build

PHONY: docker-compose-stop
docker-compose-stop:
	$(CONTAINER) compose stop

PHONY: docker
docker: docker-build docker-run

PHONY: docker-run
docker-run:
	$(CONTAINER) run $(CONTAINER_TAG)

PHONY: docker-build
docker-build:
	$(CONTAINER) build --tag $(CONTAINER_TAG):$(CONTAINER_TAG_VERSION) .
