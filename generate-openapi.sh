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
  openapi)
    gen_openapi
    ;;
  openapi_ext)
    gen_openapi_ext
    ;;
  *)
    echo "Usage: $0 {openapi|openapi_ext}"
    exit 1
    ;;
esac
