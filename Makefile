CONTAINER			:= docker
CONTAINER_COMPOSE	:= "$(CONTAINER) compose"

CONTAINER_TAG			:= sugu
CONTAINER_TAG_VERSION	:= latest
CONTAIENR_TAG_NAME		:= $(CONTAINER_TAG):$(CONTAINER_TAG_VERSION)

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
	$(CONTAINER) build --tag $(CONTAIENR_TAG_NAME) .

PHONY: docker-tag
docker-tag: docker-build
	$(CONTAINER) tag $(CONTAIENR_TAG_NAME) sirh3e/$(CONTAIENR_TAG_NAME)

PHONY: docker-push
docker-push: docker-tag
	$(CONTAINER) push sirh3e/$(CONTAIENR_TAG_NAME)