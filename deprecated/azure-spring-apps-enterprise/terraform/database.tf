# Generate Admin User for Postgresql Server
resource "random_password" "admin" {
  length  = 16
  special = false
  numeric = false
  upper   = false
}

# Generate Password for Postgresql Server
resource "random_password" "password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
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

# Create Secret for Redis Server
resource "azurerm_key_vault_secret" "cart_redis_connection_secret" {
  name         = "CART-REDIS-CONNECTION-STRING"
  value        = "rediss://:${azurerm_redis_cache.redis.primary_access_key}@${azurerm_redis_cache.redis.hostname}:${azurerm_redis_cache.redis.ssl_port}/0"
  key_vault_id = azurerm_key_vault.key_vault.id
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

# Acme Fitness Order Postgresql DB
resource "azurerm_postgresql_flexible_server_database" "postgres_order_service_db" {
  name      = var.order_service_db_name
  server_id = azurerm_postgresql_flexible_server.postgresql_server.id
  collation = "en_US.utf8"
  charset   = "utf8"
}

# Acme Fitness Catalog Postgresql DB
resource "azurerm_postgresql_flexible_server_database" "postgres_catalog_service_db" {
  name      = var.catalog_service_db_name
  server_id = azurerm_postgresql_flexible_server.postgresql_server.id
  collation = "en_US.utf8"
  charset   = "utf8"
}