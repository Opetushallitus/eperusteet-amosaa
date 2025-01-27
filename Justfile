# Generoi openapi-kuvaukset
gen_openapi:
	@cd eperusteet-amosaa-service/ \
		&& mvn clean verify -Pspringdoc \
		&& cp target/openapi/amosaa.spec.json ../generated
		
# Generoi julkiset openapi-kuvaukset
gen_openapi_ext:
	@cd eperusteet-amosaa-service/ \
		&& mvn clean verify -Pspringdoc-ext \
		&& cp target/openapi/amosaa-ext.spec.json ../generated		
