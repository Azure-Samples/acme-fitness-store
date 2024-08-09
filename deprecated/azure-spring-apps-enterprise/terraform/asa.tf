# Configure the Microsoft Azure Provider
terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }
  backend "azurerm" {
  }
}

provider "azurerm" {
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
}

locals {
  azure-metadeta = "azure.extensions"
}

data "azurerm_client_config" "current" {}

# Resource Group
resource "azurerm_resource_group" "grp" {
  name     = "${var.project_name}-grp"
  location = var.resource_group_location
}

# Log Analiytics Workspace for App Insights
resource "azurerm_log_analytics_workspace" "asa_workspace" {
  name                = "${var.project_name}-workspace"
  location            = azurerm_resource_group.grp.location
  resource_group_name = azurerm_resource_group.grp.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

# Application Insights for ASA Service
resource "azurerm_application_insights" "asa_app_insights" {
  name                = "${var.project_name}-appinsights"
  location            = azurerm_resource_group.grp.location
  resource_group_name = azurerm_resource_group.grp.name
  workspace_id        = azurerm_log_analytics_workspace.asa_workspace.id
  application_type    = "web"
}

# Azure Spring Cloud Service (ASA Service)
resource "azurerm_spring_cloud_service" "asa_service" {
  name                     = "${var.project_name}-asa"
  resource_group_name      = azurerm_resource_group.grp.name
  location                 = azurerm_resource_group.grp.location
  sku_name                 = "E0"
  service_registry_enabled = true
  build_agent_pool_size    = "S2"
  trace {
    connection_string = azurerm_application_insights.asa_app_insights.connection_string
    sample_rate       = 10.0
  }
}

# Configure Diagnostic Settings for ASA
resource "azurerm_monitor_diagnostic_setting" "asa_diagnostic" {
  name                       = "${var.project_name}-diagnostic"
  target_resource_id         = azurerm_spring_cloud_service.asa_service.id
  log_analytics_workspace_id = azurerm_log_analytics_workspace.asa_workspace.id

  enabled_log {
    category = "ApplicationConsole"
    retention_policy {
      enabled = false
      days    = 0
    }
  }
  enabled_log {
    category = "SystemLogs"
    retention_policy {
      enabled = false
      days    = 0
    }
  }
  enabled_log {
    category = "IngressLogs"
    retention_policy {
      enabled = false
      days    = 0
    }
  }

  metric {
    category = "AllMetrics"
    enabled  = true
    retention_policy {
      enabled = false
      days    = 0
    }
  }
}

# Configure Application Configuration Service for ASA
resource "azurerm_spring_cloud_configuration_service" "asa_config_svc" {
  name                    = "default"
  spring_cloud_service_id = azurerm_spring_cloud_service.asa_service.id
  repository {
    name     = "acme-fitness-store-config"
    label    = "main"
    patterns = ["catalog/default", "catalog/key-vault", "identity/default", "identity/key-vault", "payment/default"]
    uri      = "https://github.com/Azure-Samples/acme-fitness-store-config"
  }
}

# Configure Tanzu Build Service for ASA
resource "azurerm_spring_cloud_builder" "asa_builder" {
  name                    = "no-bindings-builder"
  spring_cloud_service_id = azurerm_spring_cloud_service.asa_service.id
  build_pack_group {
    name           = "default"
    build_pack_ids = ["tanzu-buildpacks/nodejs", "tanzu-buildpacks/dotnet-core", "tanzu-buildpacks/go", "tanzu-buildpacks/python"]
  }
  stack {
    id      = "io.buildpacks.stacks.bionic"
    version = "full"
  }
}

# Configure Gateway for ASA
resource "azurerm_spring_cloud_gateway" "asa_gateway" {
  name                    = "default"
  spring_cloud_service_id = azurerm_spring_cloud_service.asa_service.id
  api_metadata {
    description = "Acme Fitness Store API"
    title       = "Acme Fitness Store"
    version     = "v1.0"
  }
  cors {
    allowed_origins = ["*"]
  }
  sso {
    client_id     = var.sso-client-id
    client_secret = var.sso-client-secret
    issuer_uri    = var.sso-issuer-uri
    scope         = var.sso-scope
  }

  public_network_access_enabled = true
  instance_count                = 2
}

# Configure Api Portal for ASA
resource "azurerm_spring_cloud_api_portal" "asa_api" {
  name                    = "default"
  spring_cloud_service_id = azurerm_spring_cloud_service.asa_service.id
  gateway_ids             = [azurerm_spring_cloud_gateway.asa_gateway.id]
  sso {
    client_id     = var.sso-client-id
    client_secret = var.sso-client-secret
    issuer_uri    = var.sso-issuer-uri
    scope         = var.sso-scope
  }

  public_network_access_enabled = true
}

# Create ASA Apps Service
resource "azurerm_spring_cloud_app" "asa_app_service" {
  name = lookup(zipmap(var.asa_apps,
    tolist([var.asa_order_service,
      var.asa_cart_service,
    var.asa_frontend])),
  var.asa_apps[count.index])

  resource_group_name = azurerm_resource_group.grp.name
  service_name        = azurerm_spring_cloud_service.asa_service.name
  is_public           = true

  identity {
    type = "SystemAssigned"
  }
  count = length(var.asa_apps)
  depends_on = [azurerm_monitor_diagnostic_setting.asa_diagnostic, azurerm_spring_cloud_configuration_service.asa_config_svc,
  azurerm_spring_cloud_builder.asa_builder, azurerm_spring_cloud_gateway.asa_gateway, azurerm_spring_cloud_api_portal.asa_api]
}


# Create ASA Apps Service with Tanzu Component binds
resource "azurerm_spring_cloud_app" "asa_app_service_bind" {
  name = lookup(zipmap(var.asa_apps_bind,
    tolist([var.asa_catalog_service,
      var.asa_payment_service,
    var.asa_identity_service])),
  var.asa_apps_bind[count.index])

  resource_group_name = azurerm_resource_group.grp.name
  service_name        = azurerm_spring_cloud_service.asa_service.name
  is_public           = true

  identity {
    type = "SystemAssigned"
  }

  addon_json = jsonencode({
    applicationConfigurationService = {
      resourceId = azurerm_spring_cloud_configuration_service.asa_config_svc.id
    }
    serviceRegistry = {
      resourceId = azurerm_spring_cloud_service.asa_service.service_registry_id
    }
  })

  count = length(var.asa_apps_bind)
  depends_on = [azurerm_monitor_diagnostic_setting.asa_diagnostic, azurerm_spring_cloud_configuration_service.asa_config_svc,
  azurerm_spring_cloud_builder.asa_builder, azurerm_spring_cloud_gateway.asa_gateway, azurerm_spring_cloud_api_portal.asa_api]
}

# Create ASA Apps Deployment
resource "azurerm_spring_cloud_build_deployment" "asa_app_deployment" {
  name = "default"
  spring_cloud_app_id = concat(azurerm_spring_cloud_app.asa_app_service,
  azurerm_spring_cloud_app.asa_app_service_bind)[count.index].id
  build_result_id = "<default>"

  quota {
    cpu    = "1"
    memory = "1Gi"
  }
  count = sum([length(var.asa_apps), length(var.asa_apps_bind)])
}

# Activate ASA Apps Deployment
resource "azurerm_spring_cloud_active_deployment" "asa_app_deployment_activation" {
  spring_cloud_app_id = concat(azurerm_spring_cloud_app.asa_app_service,
  azurerm_spring_cloud_app.asa_app_service_bind)[count.index].id
  deployment_name = azurerm_spring_cloud_build_deployment.asa_app_deployment[count.index].name

  count = sum([length(var.asa_apps), length(var.asa_apps_bind)])
}

# Postgres Flexible Server Connector for Order Service
resource "azurerm_spring_cloud_connection" "asa_app_order_connection" {
  name               = "order_service_db"
  spring_cloud_id    = azurerm_spring_cloud_build_deployment.asa_app_deployment[0].id
  target_resource_id = azurerm_postgresql_flexible_server_database.postgres_order_service_db.id
  client_type        = "dotnet"
  authentication {
    type   = "secret"
    name   = random_password.admin.result
    secret = random_password.password.result
  }
}

# Postgres Flexible Server Connector for Catalog Service
resource "azurerm_spring_cloud_connection" "asa_app_catalog_connection" {
  name               = "catalog_service_db"
  spring_cloud_id    = azurerm_spring_cloud_build_deployment.asa_app_deployment[3].id
  target_resource_id = azurerm_postgresql_flexible_server_database.postgres_catalog_service_db.id
  client_type        = "springBoot"
  authentication {
    type = "systemAssignedIdentity"
  }
}

# Create Routing for Catalog Service
resource "azurerm_spring_cloud_gateway_route_config" "asa_app_catalog_routing" {
  name                    = var.asa_catalog_service
  spring_cloud_gateway_id = azurerm_spring_cloud_gateway.asa_gateway.id
  spring_cloud_app_id     = azurerm_spring_cloud_app.asa_app_service_bind[0].id
  route {
    filters             = ["StripPrefix=0"]
    order               = 100
    predicates          = ["Path=/products", "Method=GET"]
    classification_tags = ["catalog"]
  }
  route {
    filters             = ["StripPrefix=0"]
    order               = 101
    predicates          = ["Path=/products/{id}", "Method=GET"]
    classification_tags = ["catalog"]
  }
  route {
    filters             = ["StripPrefix=0", "SetPath=/actuator/health/liveness"]
    order               = 103
    predicates          = ["Path=/catalogliveness", "Method=GET"]
    classification_tags = ["catalog"]
  }
  route {
    filters             = ["StripPrefix=0"]
    order               = 104
    predicates          = ["Path=/static/images/{id}", "Method=GET"]
    classification_tags = ["catalog"]
  }
  depends_on = [azurerm_spring_cloud_active_deployment.asa_app_deployment_activation]
}

# Create Routing for Order Service
resource "azurerm_spring_cloud_gateway_route_config" "asa_app_order_routing" {
  name                    = var.asa_order_service
  spring_cloud_gateway_id = azurerm_spring_cloud_gateway.asa_gateway.id
  spring_cloud_app_id     = azurerm_spring_cloud_app.asa_app_service[0].id
  route {
    description            = "Creates an order for the user."
    filters                = ["StripPrefix=0"]
    order                  = 200
    predicates             = ["Path=/order/add/{userId}", "Method=POST"]
    sso_validation_enabled = true
    title                  = "Create an order."
    token_relay            = true
    classification_tags    = ["order"]
  }
  route {
    description            = "Lookup all orders for the given user"
    filters                = ["StripPrefix=0"]
    order                  = 201
    predicates             = ["Path=/order/{userId}", "Method=GET"]
    sso_validation_enabled = true
    title                  = "Retrieve User's Orders."
    token_relay            = true
    classification_tags    = ["order"]
  }
  depends_on = [azurerm_spring_cloud_active_deployment.asa_app_deployment_activation]
}

# Create Routing for Cart Service
resource "azurerm_spring_cloud_gateway_route_config" "asa_app_cart_routing" {
  name                    = var.asa_cart_service
  spring_cloud_gateway_id = azurerm_spring_cloud_gateway.asa_gateway.id
  spring_cloud_app_id     = azurerm_spring_cloud_app.asa_app_service[1].id
  route {
    filters                = ["StripPrefix=0"]
    order                  = 300
    predicates             = ["Path=/cart/item/add/{userId}", "Method=POST"]
    sso_validation_enabled = true
    token_relay            = true
    classification_tags    = ["cart"]
  }
  route {
    filters                = ["StripPrefix=0"]
    order                  = 301
    predicates             = ["Path=/cart/item/modify/{userId}", "Method=POST"]
    sso_validation_enabled = true
    token_relay            = true
    classification_tags    = ["cart"]
  }
  route {
    filters                = ["StripPrefix=0"]
    order                  = 302
    predicates             = ["Path=/cart/items/{userId}", "Method=GET"]
    sso_validation_enabled = true
    token_relay            = true
    classification_tags    = ["cart"]
  }
  route {
    filters                = ["StripPrefix=0"]
    order                  = 303
    predicates             = ["Path=/cart/clear/{userId}", "Method=GET"]
    sso_validation_enabled = true
    token_relay            = true
    classification_tags    = ["cart"]
  }
  route {
    filters                = ["StripPrefix=0"]
    order                  = 304
    predicates             = ["Path=/cart/total/{userId}", "Method=GET"]
    sso_validation_enabled = true
    token_relay            = true
    classification_tags    = ["cart"]
  }
  depends_on = [azurerm_spring_cloud_active_deployment.asa_app_deployment_activation]
}

# Create Routing for Identity Service
resource "azurerm_spring_cloud_gateway_route_config" "asa_app_identity_routing" {
  name                    = var.asa_identity_service
  spring_cloud_gateway_id = azurerm_spring_cloud_gateway.asa_gateway.id
  spring_cloud_app_id     = azurerm_spring_cloud_app.asa_app_service_bind[2].id
  route {
    filters                = ["RedirectTo=302, /"]
    order                  = 1
    predicates             = ["Path=/acme-login", "Method=GET"]
    sso_validation_enabled = true
    classification_tags    = ["sso"]
  }
  route {
    filters                = ["RedirectTo=302, /whoami", "SetResponseHeader=Cache-Control, no-store"]
    order                  = 2
    predicates             = ["Path=/userinfo", "Method=GET"]
    sso_validation_enabled = true
    token_relay            = true
    classification_tags    = ["users"]
  }
  route {
    order                  = 3
    predicates             = ["Path=/verify-token", "Method=POST"]
    sso_validation_enabled = true
    uri                    = "no://op"
    classification_tags    = ["users"]
  }
  route {
    filters                = ["StripPrefix=0"]
    order                  = 4
    predicates             = ["Path=/whoami", "Method=GET"]
    sso_validation_enabled = true
    token_relay            = true
    classification_tags    = ["users"]

  }
  depends_on = [azurerm_spring_cloud_active_deployment.asa_app_deployment_activation]
}

# Create Routing for Frontend
resource "azurerm_spring_cloud_gateway_route_config" "asa_app_frontend_routing" {
  name                    = var.asa_frontend
  spring_cloud_gateway_id = azurerm_spring_cloud_gateway.asa_gateway.id
  spring_cloud_app_id     = azurerm_spring_cloud_app.asa_app_service[2].id
  route {
    filters             = ["StripPrefix=0"]
    order               = 1000
    predicates          = ["Path=/**", "Method=GET"]
    classification_tags = ["frontend"]
  }
  depends_on = [azurerm_spring_cloud_active_deployment.asa_app_deployment_activation]
}

