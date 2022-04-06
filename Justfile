# Generoi openapi-kuvaukset
gen_openapi:
	@cd eperusteet-amosaa-service/ \
		&& mvn clean compile -P generate-openapi \
		&& cp target/openapi/amosaa.spec.json ../generated
		
# Generoi julkiset openapi-kuvaukset
gen_openapi_ext:
	@cd eperusteet-amosaa-service/ \
		&& mvn clean compile -P generate-openapi-ext \
		&& cp target/openapi/amosaa-ext.spec.json ../generated		
