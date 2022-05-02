# Variable Definition 
variable "project_name" {
  type        = string
  default     = "acme-fitness"
  description = "Project Name"
}

variable "resource_group_location" {
  type        = string
  default     = "West Europe"
  description = "Azure Resource Group"
}

variable "postgres_db_name" {
  type    = list(any)
  default = ["acmefit_catalog", "acmefit_order"]
}
