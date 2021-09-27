ODF_DIR=../../open-data-fabric

.PHONY: codegen
codegen:
	python $(ODF_DIR)/tools/jsonschema_to_scala.py $(ODF_DIR)/schemas > src/main/scala/dev/kamu/core/manifests/OpenDataFabric.scala
