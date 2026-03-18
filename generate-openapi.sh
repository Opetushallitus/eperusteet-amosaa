#!/bin/bash
set -euo pipefail

# Generoi openapi-kuvaukset
gen_openapi() {
  cd eperusteet-amosaa-service/ \
    && mvn clean verify -Pspringdoc \
    && cp target/openapi/amosaa.spec.json ../generated
  cd - > /dev/null
}

# Generoi julkiset openapi-kuvaukset
gen_openapi_ext() {
  cd eperusteet-amosaa-service/ \
    && mvn clean verify -Pspringdoc-ext \
    && cp target/openapi/amosaa-ext.spec.json ../generated
  cd - > /dev/null
}

# Dispatch based on argument
case "${1:-}" in
  ext)
    gen_openapi_ext
    ;;
  "")
    gen_openapi
    ;;
  *)
    echo "Usage: $0 [ext]"
    echo "  (no args)  - generate standard OpenAPI spec"
    echo "  ext       - generate extended/public OpenAPI spec"
    exit 1
    ;;
esac
