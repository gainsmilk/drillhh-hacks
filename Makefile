SHELL := /bin/bash

GRADLE  := ./gradlew
MODS    := $(HOME)/.weave/mods
JAR_DIR := build/libs

.PHONY: help build deploy install clean changelog

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'

build: ## Build the mod jar (output: build/libs/)
	$(GRADLE) build

deploy: build ## Build + copy jar to ~/.weave/mods/
	@mkdir -p $(MODS)
	@cp $(JAR_DIR)/trenbolone-bridgonate-*.jar $(MODS)/
	@echo "deployed to $(MODS)"

install: deploy ## Alias for deploy

clean: ## Clean build artifacts
	$(GRADLE) clean

changelog: ## Generate changelog from conventional commits
	@command -v git-cliff > /dev/null 2>&1 && git-cliff -o CHANGELOG.md || echo "install git-cliff: cargo install git-cliff"
