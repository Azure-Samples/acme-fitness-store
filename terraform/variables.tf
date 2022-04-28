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

variable "dbadmin" {
  type        = string
  default     = "posgredbadmin"
  description = "Admin User for PosgreSql Server"
  sensitive   = true
}

variable "dbpassword" {
  type        = string
  description = "Password for Admin User for PosgreSql Server"
  sensitive   = true
}

variable "postgres_db_name" {
  type    = list(any)
  default = ["acmefit_catalog", "acmefit_order"]
}
