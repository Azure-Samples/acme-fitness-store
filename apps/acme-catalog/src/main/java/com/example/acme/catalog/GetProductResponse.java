package com.example.acme.catalog;

public class GetProductResponse {

    private ProductValueObject data;
    private int status;

    public GetProductResponse(ProductValueObject data, int status) {
        this.data = data;
        this.status = status;
    }

    public ProductValueObject getData() {
        return data;
    }

    public void setData(ProductValueObject data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
