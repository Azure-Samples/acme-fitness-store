#!/bin/bash
set -x
az spring app deploy -n ${APP_NAME} -g ${RESOURCE_GROUP} -s ${SPRING_APPS_SERVICE} --source-path ../05-deploy-app/hello-world