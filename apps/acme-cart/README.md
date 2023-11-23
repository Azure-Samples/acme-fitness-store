# Cart


> A cart service, because what is a shop without a cart to put stuff in?

The Cart service is part of the [ACME Fitness Shop](https://github.com/Azure-Samples/acme-fitness-store). The goal of this specific service is to keep track of carts and items in the different carts.

## Prerequisites

There are different dependencies based on whether you want to run a built container, or build a new one.

### Build

* [Python 3.7.2 or higher](https://www.python.org/downloads/)
* [Pip](https://pip.pypa.io/en/stable/installing/)
* pyenv 
### Run

* [Docker](https://www.docker.com/docker-community)
* [Redis](https://hub.docker.com/r/bitnami/redis)

## Installation

### Source

A pyenv `.python-version` configuration file is provided. You can 
configure a compatible environment using `pyenv` by running `pyenv install`.

To build the app as a stand-alone executable, run `pip install -r requirements.txt` to install the Python libraries and run `python3 cart.py` after.

By default, the app runs on port 5000 which is used by MacOS for airplay service. If you get a port conflict change the port to `5001` by setting the environment variable `CART_PORT=5001 ptthon3 cart.py`

## Usage

The **cart** service, either running inside a Docker container or as a stand-alone app, relies on the below environment variables:

* **REDIS_HOST**: The hostname or IP address to connect to the Redis server (defaults to `localhost`)
* **REDIS_PORT**: The port to connect to the Redis server (defaults to `6379`)
* **REDIS_PASSWORD**: The password to connect to Redis (defaults to `blank`)
* **CART_PORT**: The port number the cart service will listen to requests (defaults to `5000`)

The Docker image is based on the Bitnami Python container. Use this commands to run the latest stable version of the payment service with all available parameters:

```bash
# Run the Redis container
docker run -p 6379:6379 bitnami/redis:latest

# Run the cart service
docker run --rm -it -e REDIS_HOST=localhost -e REDIS_PORT=6379 -e REDIS_PASSWORD=myAwesomePassword -e CART_PORT=5000 -p 5000:5000 gcr.io/vmwarecloudadvocacy/acmeshop-cart:stable
```

## API

### HTTP

#### `GET /cart/total/<userid>`

Get total amount in users cart

```bash
curl --request GET \
  --url http://localhost:5000/cart/total/dan
```

```json
{
  "carttotal": 804.5,
  "userid": "dan"
}
```

#### `POST /cart/item/modify/<userid>`

Update an item in the cart of a user

```bash
curl --request POST \
  --url http://localhost:5000/cart/item/modify/dan \
  --header 'content-type: application/json' \
  --data '{"itemid":"sfsdsda3343", "quantity":2}'
```

To modify the item in a cart, the input needs to contain an `itemid` and the new `quantity`

```json
{"itemid":"sfsdsda3343", "quantity":2}
```

A successful update will return the userid

```json
{
  "userid": "dan"
}
```

#### `POST /cart/modify/<userid>`

Modify the contents of a cart

```bash
curl --request POST \
  --url http://localhost:5000/cart/modify/dan \
  --header 'content-type: application/json' \
  --data '{
  "cart": [
    {
      "description": "fitband for any age - even babies",
      "itemid": "sdfsdfsfs",
      "name": "fitband",
      "price": 4.5,
      "quantity": 1
    },
    {
      "description": "the most awesome redpants in the world",
      "itemid": "sfsdsda3343",
      "name": "redpant",
      "price": 400,
      "quantity": 1
    }
  ],
  "userid": "dan"
}'
```

To replace the entire cart, or create a new cart for a user, send a cart object

```json
{
  "cart": [
    {
      "description": "fitband for any age - even babies",
      "itemid": "sdfsdfsfs",
      "name": "fitband",
      "price": 4.5,
      "quantity": 1
    }
  ],
  "userid": "dan"
}
```

A successful update will return the userid

```json
{
  "userid": "dan"
}
```

#### `POST /cart/item/add/<userid>`

Add item to cart

```bash
curl --request POST \
  --url http://localhost:5000/cart/item/add/shri \
  --header 'content-type: application/json' \
  --data '{"itemid":"xyz", "quantity":3}'
```

To add the item in a cart, the input needs to contain an `itemid` and the `quantity`

```json
{"itemid":"xyz", "quantity":3}
```

A successful update will return the userid

```json
{
  "userid": "shri"
}
```

#### `GET /cart/items/total/<userid>`

Get the total number of items in a cart

```bash
curl --request GET \
  --url http://localhost:5000/cart/items/total/shri
```

```json
{
  "cartitemtotal": 5.0,
  "userid": "shri"
}
```

#### `GET /cart/clear/<userid>`

Clear all items from the cart

```bash
curl --request GET \
  --url http://localhost:5000/cart/clear/dan
```

```text
<no payload returned>
```

#### `GET /cart/items/<userid>`

Get all items in a cart

```bash
curl --request GET \
  --url http://localhost:5000/cart/items/dan
```

```json
{
  "cart": [
    {
      "description": "fitband for any age - even babies",
      "itemid": "sdfsdfsfs",
      "name": "fitband",
      "price": 4.5,
      "quantity": 1
    },
    {
      "description": "the most awesome redpants in the world",
      "itemid": "sfsdsda3343",
      "name": "redpant",
      "price": 400,
      "quantity": 1
    }
  ],
  "userid": "dan"
}
```

#### `GET /cart/all`

Get all the carts

```bash
curl --request GET \
  --url http://localhost:5000/cart/all
```

```json
{
  "all carts": [
    {
      "cart": [
        {
          "description": "fitband for any age - even babies",
          "itemid": "sdfsdfsfs",
          "name": "fitband",
          "price": 4.5,
          "quantity": 1
        },
        {
          "description": "the most awesome redpants in the world",
          "itemid": "sfsdsda3343",
          "name": "redpant",
          "price": 400,
          "quantity": 1
        }
      ],
      "id": "shri"
    }
  ]
}
```

## Contributing

[Pull requests](https://github.com/Azure-Samples/acme-fitness-store/pulls) are welcome. For major changes, please open [an issue](https://github.com/Azure-Samples/acme-fitness-store/issues) first to discuss what you would like to change.

Please make sure to update tests as appropriate.