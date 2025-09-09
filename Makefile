.DEFAULT_GOAL := build

.PHONY: build
build:
	docker build -t jz/faulty-tower-elevator:latest .

.PHONY: test
test:
	docker run jz/faulty-tower-elevator:latest