#!/usr/bin/python
from os import environ
from azure.keyvault.secrets import SecretClient
from azure.identity import DefaultAzureCredential

URI_KEY = 'KEYVAULT_URI'


def vault_secret(key):
    if environ.get(URI_KEY) not in (None, ''):
        client = SecretClient(vault_url=environ[URI_KEY], credential=DefaultAzureCredential())
        secrets = client.list_properties_of_secrets()

        for secret in secrets:
            if secret.name == key:
                return client.get_secret(key).value

    return None
