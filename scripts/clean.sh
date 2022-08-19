#!/usr/bin/env sh
set -eux

gradle_path=".gradle"
env_path=".env"
makefile_path="Makefile.env"

rm -rfv "${gradle_path}" "${env_path}" "${makefile_path}"