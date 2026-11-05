#!/bin/sh
set -eu

if [ -z "${BACKEND_URL:-}" ]; then
    echo "Set BACKEND_URL to the HTTPS origin of the deployed Spring Boot service." >&2
    exit 1
fi

case "$BACKEND_URL" in
    https://?*) ;;
    *)
        echo "BACKEND_URL must be an HTTPS origin, for example https://apna-academy.onrender.com." >&2
        exit 1
        ;;
esac

backend_url=${BACKEND_URL%/}
case "$backend_url" in
    https://*/*|https://*\?*|https://*\#*|https://*@*)
        echo "BACKEND_URL must contain only the backend origin, without a path, query, credentials, or fragment." >&2
        exit 1
        ;;
esac

mkdir -p public
printf '/* %s/:splat 200!\n' "$backend_url" > public/_redirects
