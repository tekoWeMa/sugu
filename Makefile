CONTAINER		:= docker
CONTAINER_TAG	:= sugu

PHONY: docker
docker: docker-build docker-run

PHONY: docker-run
docker-run:
	$(CONTAINER) run $(CONTAINER_TAG)

PHONY: docker-build
docker-build:
	$(CONTAINER) build --tag $(CONTAINER_TAG) .
