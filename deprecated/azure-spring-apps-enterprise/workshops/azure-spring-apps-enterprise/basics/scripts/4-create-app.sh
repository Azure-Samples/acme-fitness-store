#!/bin/bash

set -x
az spring app create -n ${APP_NAME} -g ${RESOURCE_GROUP} -s ${SPRING_APPS_SERVICE} 