# Variable Definition 
variable "project_name" {
  type        = string
  default     = "fitness-store"
  description = "Project Name"
}

variable "resource_group_location" {
  type        = string
  default     = "West Europe"
  description = "Azure Resource Group"
}

variable "asa_cart_service" {
  type        = string
  default     = "cart-service"
  description = "Cart Service App Name"
}

variable "asa_order_service" {
  type        = string
  default     = "order-service"
  description = "Order Service App Name"
}

variable "asa_payment_service" {
  type        = string
  default     = "payment-service"
  description = "Payment Service App Name"
}

variable "asa_catalog_service" {
  type        = string
  default     = "catalog-service"
  description = "Catalog Service App Name"
}

variable "asa_frontend" {
  type        = string
  default     = "frontend"
  description = "Frontend App Name"
}

variable "asa_identity_service" {
  type        = string
  default     = "identity-service"
  description = "Identity Service App Name"
}

variable "asa_apps" {
  type        = list(string)
  default     = ["catalog_service", "payment_service", "identity_service"]
  description = "Varible used as keys to create apps"
}

variable "asa_apps_bind" {
  type        = list(string)
  default     = ["order_service", "cart_service", "frontend"]
  description = "Varible used as keys to create apps with Tanzu Component Binds"
}

variable "order_service_db_name" {
  type    = string
  default = "acmefit_order"
}

variable "catalog_service_db_name" {
  type    = string
  default = "acmefit_catalog"
}

variable "sso-jwk-uri" {
  type        = string
  description = "SSO Provider JWK-URI"
}

variable "sso-client-id" {
  type        = string
  description = "SSO Provider Client ID"
}

variable "sso-client-secret" {
  type        = string
  description = "SSO Provider Client Secret"
}

variable "sso-issuer-uri" {
  type        = string
  description = "SSO Provider Issuer URI"
}

variable "sso-scope" {
  type        = list(string)
  default     = ["openid", "profile", "email"]
  description = "SSO Provider Scope"
}