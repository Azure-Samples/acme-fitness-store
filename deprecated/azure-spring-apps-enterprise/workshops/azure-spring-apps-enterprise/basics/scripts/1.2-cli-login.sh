#!/bin/bash
set -x
az login --use-device-code
az account list -o table
az account set --subscription ${SUBSCRIPTION_ID}