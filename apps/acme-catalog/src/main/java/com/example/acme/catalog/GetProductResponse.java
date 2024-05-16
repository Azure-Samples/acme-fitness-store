package com.example.acme.catalog;

public class GetProductResponse {

    private ProductVo data;
    private int status;

    public GetProductResponse(ProductVo data, int status) {
        this.data = data;
        this.status = status;
    }

    public ProductVo getData() {
        return data;
    }

    public void setData(ProductVo data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
