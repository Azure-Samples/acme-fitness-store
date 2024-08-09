#!/bin/bash
set -x
az group create --name ${RESOURCE_GROUP} \
    --location ${REGION}