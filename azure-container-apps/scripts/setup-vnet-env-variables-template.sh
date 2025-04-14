# Resource group and environment names
SUBSCRIPTION='subscription-id'                 # replace it with your subscription-id
PREFIX='unique-prefix'                         # unique prefix for all resources(not use special characters)
RESOURCE_GROUP="${PREFIX}-rg"
LOCATION='eastus2'

# Run `az ad signed-in-user show --query id --output tsv` to get the current user object id
CURRENT_USER_OBJECT_ID='your-current-user-object-id' # replace it with your current user object id

VNET_NAME="${PREFIX}-aca-vnet"
VNET_ADDRESS_PREFIX='10.0.0.0/16'
 
ACA_SUBNET_NAME="aca-subnet"
ACA_ADDRESS_PREFIX='10.0.0.0/23'
 
VM_SUBNET_NAME="vm-subnet"
VM_ADDRESS_PREFIX='10.0.2.0/23'
 
PRIVATE_ENDPOINT_SUBNET_NAME="private-endpoint-subnet"
PRIVATE_ENDPOINT_ADDRESS_PREFIX='10.0.4.0/23'