export SUBSCRIPTION='subscription-id'                 # replace it with your subscription-id
export RESOURCE_GROUP='resource-group-name'           # existing resource group or one that will be created in next steps
export SPRING_APPS_SERVICE='azure-spring-apps-name'   # name of the service that will be created in the next steps
export LOG_ANALYTICS_WORKSPACE='log-analytics-name'   # existing workspace or one that will be created in next steps
export REGION='region-name'                           # choose a region with Enterprise tier support

export CART_SERVICE_APP='cart-service'
export IDENTITY_SERVICE_APP='identity-service'
export ORDER_SERVICE_APP='order-service'
export PAYMENT_SERVICE_APP='payment-service'
export CATALOG_SERVICE_APP='catalog-service'
export FRONTEND_APP='frontend'

export CUSTOM_BUILDER='no-bindings-builder'

echo "SUBSCRIPTION=$SUBSCRIPTION"
echo "RESOURCE_GROUP=$RESOURCE_GROUP"
echo "SPRING_APPS_SERVICE=$SPRING_APPS_SERVICE"
echo "REGION=$REGION"
echo "CUSTOM_BUILDER=$CUSTOM_BUILDER"

az account set --subscription ${SUBSCRIPTION}