# Configure the Microsoft Azure Provider
terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }

  cloud {
    organization = "acme-fitness-store"
    workspaces {
      name = "development"
    }
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

# Generate Admin User for Postgresql Server
resource "random_password" "admin" {
  length  = 16
  special = false
  number  = false
  upper   = false
}

# Generate Password for Postgresql Server
resource "random_password" "password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

# Resource Group
resource "azurerm_resource_group" "grp" {
  name     = "${var.project_name}-resources"
  location = var.resource_group_location
}

# Azure Cache for Redis Instance
resource "azurerm_redis_cache" "redis" {
  name                = "${var.project_name}-redis"
  location            = azurerm_resource_group.grp.location
  resource_group_name = azurerm_resource_group.grp.name
  capacity            = 1
  family              = "C"
  sku_name            = "Basic"
}

# Postgresql Flexible Server
resource "azurerm_postgresql_flexible_server" "postgresql_server" {
  name                   = "${var.project_name}-db-server"
  resource_group_name    = azurerm_resource_group.grp.name
  location               = azurerm_resource_group.grp.location
  version                = "13"
  administrator_login    = random_password.admin.result
  administrator_password = random_password.password.result
  sku_name               = "GP_Standard_D4s_v3"
  storage_mb             = 32768
  zone                   = "1"
}

# Allow connections from other Azure Services
resource "azurerm_postgresql_flexible_server_firewall_rule" "postgresql_server_fw" {
  name             = "${var.project_name}-db-server-fw"
  server_id        = azurerm_postgresql_flexible_server.postgresql_server.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

# Enable the uuid-ossp extension
resource "azurerm_postgresql_flexible_server_configuration" "postgresql_server_config" {
  name      = local.azure-metadeta
  server_id = azurerm_postgresql_flexible_server.postgresql_server.id
  value     = "uuid-ossp"
}

# Acme Fitness Catalog & Order Postgresql DB
resource "azurerm_postgresql_flexible_server_database" "postgre_db" {
  name      = var.postgres_db_name[count.index]
  server_id = azurerm_postgresql_flexible_server.postgresql_server.id
  collation = "en_US.utf8"
  charset   = "utf8"
  count     = length(var.postgres_db_name)
}

# Log Analiytics Workspace for App Insights
resource "azurerm_log_analytics_workspace" "asc_workspace" {
  name                = "${var.project_name}-workspace"
  location            = azurerm_resource_group.grp.location
  resource_group_name = azurerm_resource_group.grp.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

# Keyvault for Saving Secrets
resource "azurerm_key_vault" "key_vault" {
  name                       = "${var.project_name}-keyvault"
  location                   = azurerm_resource_group.grp.location
  resource_group_name        = azurerm_resource_group.grp.name
  tenant_id                  = data.azurerm_client_config.current.tenant_id
  sku_name                   = "standard"
  soft_delete_retention_days = 7

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id

    secret_permissions = [
      "Set",
      "Get",
      "List",
      "Delete",
      "Purge",
      "Recover"
    ]
  }
}

# Create Secret for Admin Username
resource "azurerm_key_vault_secret" "admin_username" {
  name         = "admin-username"
  value        = azurerm_postgresql_flexible_server.postgresql_server.administrator_login
  key_vault_id = azurerm_key_vault.key_vault.id
}

# Create Secret for Admin Password
resource "azurerm_key_vault_secret" "admin_password" {
  name         = "admin-password"
  value        = azurerm_postgresql_flexible_server.postgresql_server.administrator_password
  key_vault_id = azurerm_key_vault.key_vault.id
}
