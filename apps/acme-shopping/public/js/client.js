
// Returns the full name for a user based on userid
function getUserInfo(handleUserInfo) {
    console.log('Requesting user details from backend');

    $.ajax({
        url: "https://acme-fitness-store-gateway-7d0d7.svc.azuremicroservices.io/userinfo",
        type: 'GET',
        success: function (json) {
            if (json) {
                console.debug('type of body', typeof json, json);
                handleUserInfo(json);
            } else {
                console.error('Could not get user information', json);
                return handleUserInfo({});
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error('Could not get user information due to: ' + textStatus + ' | ' + errorThrown);
        }
    });

}

// Delete an item from the cart
function deleteItem(itemid, userid) {

    console.log('Deleting item from cart');

    vals = {
        "itemid" : itemid,
        "quantity": 0
    }

    $.ajax({
        url: "https://acme-fitness-store-gateway-7d0d7.svc.azuremicroservices.io/cart/item/modify/" + userid,
        type: "POST",
        data: JSON.stringify(vals),
        success: function(data, textStatus, jqXHR) {

            console.log('Item deleted successfully ')
            location.reload();

        },
        error: function(jqXHR, textStatus, errorThrown) {

            console.log('Error while deleting item from cart ' + textStatus + ' | ' + errorThrown)
        }
    });

}

// Modify the item in a cart
function updateCart(itemid, quantity, userid) {

    console.log('Updating item from cart ' + itemid);

    vals = {
        "itemid" : itemid,
        "quantity": quantity
    }

    $.ajax({

        url: "https://acme-fitness-store-gateway-7d0d7.svc.azuremicroservices.io/cart/item/modify/" + userid,
        type: "POST",
        data: JSON.stringify(vals),
        success: function(data, textStatus, jqXHR) {

            console.log('Modified cart item ' + textStatus);
            location.reload();

        },
        error: function(jqXHR, textStatus, errorThrown) {

            console.log('Error modifying item quantity from cart ' + textStatus + ' | ' + errorThrown)

        }
    });

}


// Get the total for cart
function getCartTotal(userid) {

    var cartTotal = 0
    $.ajax({

        url: "https://acme-fitness-store-gateway-7d0d7.svc.azuremicroservices.io/cart/total/" + userid,
        type: "GET",
        async: false,
        success: function(body, textStatus, jqXHR) {

            if (jqXHR.status == 200){

                cartTotal = body.carttotal;
            }
            else {
                console.log('Could not get total ' + textStatus);
                cartTotal = 0;
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log('Error Receiving Total ' + errorThrown);
        }

    });

    return cartTotal;

}

function getUserID() {
    var userId = $.cookie('user_id')
    return userId || 'guest'
}

// Returns image url
function getImageUrl(productId, setUrl) {

    var imageurl = ''
    $.ajax({
        url: "https://acme-fitness-store-gateway-7d0d7.svc.azuremicroservices.io/products/" + productId,
        type: 'GET',
        async: false,
        success: function (body, textStatus, jqXHR) {
            console.log('Getting Product info ' + body)
            if (jqXHR.status == 200) {

                imageurl = body.data.imageUrl1;

            } else {

                console.error('Could not get product image: ' + textStatus);
                imageurl = undefined;
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error('Could not get product information: ' + productId + ', due to: ' + textStatus + ' | ' + errorThrown);
        }
    });

    return imageurl
}