package com.example.acme.catalog;

import java.util.List;

public class GetProductsResponse {

    private List<ProductVo> data;

    public GetProductsResponse(List<ProductVo> data) {
        this.data = data;
    }

    public List<ProductVo> getData() {
        return data;
    }

    public void setData(List<ProductVo> data) {
        this.data = data;
    }
}
