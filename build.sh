#!/bin/bash

set -euxo pipefail

mvn --show-version clean package -DskipTests -Dquarkus.container-image.build=true

echo "end of build.sh" > /dev/null
