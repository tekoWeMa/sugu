#!/usr/bin/env sh
set -eux

env_path=".env"
makefile_path="Makefile.env"

if [ ! -f "${env_path}" ]
then
  echo "Please provide: DISCORD_CLIENT_TOKEN"
  read -r token
  echo "DISCORD_CLIENT_TOKEN=${token}" > "${env_path}"
fi

if [ ! -f "${makefile_path}" ]
then
  cp "${env_path}" "${makefile_path}"
fi