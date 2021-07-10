#!/bin/sh

operation="$1"

set -- 8081 8082 8090 8095

if [ "$operation" = "provision" ]; then
    for port in "$@"; do
        docker run --detach --rm \
            --publish "$port":5678 \
            --name server-"$port" \
            hashicorp/http-echo -text="hello world"
    done

    exit 0
fi

if [ "$operation" = "destroy" ]; then
    for port in "$@"; do
        docker stop server-"$port"
    done

    exit 0
fi

printf "Unknown operation: %s\n" "$operation"
printf "It should be %s or %s\n" "provision" "destroy"

exit 1

