package com.example.acme.catalog;

import java.util.List;

public class GetProductsResponse {

    private List<ProductValueObject> data;

    public GetProductsResponse(List<ProductValueObject> data) {
        this.data = data;
    }

    public List<ProductValueObject> getData() {
        return data;
    }

    public void setData(List<ProductValueObject> data) {
        this.data = data;
    }
}
