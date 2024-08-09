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

# Create Secret for SSO Provider JWK URI
resource "azurerm_key_vault_secret" "sso_jwk_uri_secret" {
  name         = "SSO-PROVIDER-JWK-URI"
  value        = var.sso-jwk-uri
  key_vault_id = azurerm_key_vault.key_vault.id
}