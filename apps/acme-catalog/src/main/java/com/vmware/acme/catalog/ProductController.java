package com.vmware.acme.catalog;

import java.util.stream.Collectors;

import io.micrometer.core.annotation.Timed;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Timed("store.products")
@RestController
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/products")
	public GetProductsResponse getProducts() {
		return new GetProductsResponse(productService.getProducts().stream()
													 .map(ProductResponse::new)
													 .collect(Collectors.toList()));
	}

	@GetMapping("/products/{id}")
	public GetProductResponse getProduct(@PathVariable String id) {
		return new GetProductResponse(new ProductResponse(productService.getProduct(id)), HttpStatus.OK.value());
	}
}
