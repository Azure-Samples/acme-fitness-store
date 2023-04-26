export SUBSCRIPTION=Project Pack MSLS-4675                 # replace it with your subscription-id
export RESOURCE_GROUP=asc-training-rg-92          # existing resource group or one that will be created in next steps
export SPRING_APPS_SERVICE=asc-training-asae-ifdu-92   # A unique name of the service that will be created in the next steps
export REGION=eastus                          # choose a region with Enterprise tier support

export CART_SERVICE_APP="cart-service"
export IDENTITY_SERVICE_APP="identity-service"
export ORDER_SERVICE_APP="order-service"
export PAYMENT_SERVICE_APP="payment-service"
export CATALOG_SERVICE_APP="catalog-service"
export FRONTEND_APP="frontend"

echo "export SUBSCRIPTION=$SUBSCRIPTION"
echo "export RESOURCE_GROUP=$RESOURCE_GROUP"
echo "export SPRING_APPS_SERVICE=$SPRING_APPS_SERVICE"
echo "export REGION=$REGION"
