package com.example.acme.catalog;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static java.util.stream.StreamSupport.stream;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> getProducts() {
		return stream(productRepository.findAll().spliterator(), false)
				.collect(Collectors.toList());
	}

	public Product getProduct(String id) {
		return productRepository.findById(id)
								.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find product with id " + id));

	}

	public Product createProduct(Product product) {
		return productRepository.save(product);
	}

	public Product updateProduct(String id, Product product) {
		Product updatedEntity = productRepository.findById(id)
				.map(entity -> updateProductEntity(entity, product))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find product with id " + id));

		return productRepository.save(updatedEntity);
	}

	public void deleteProduct(String id) {
		productRepository.deleteById(id);
	}

	private static Product updateProductEntity(Product existing, Product product) {
		updateIfNotNull(product.getImageUrl1(), existing::setImageUrl1);
		updateIfNotNull(product.getImageUrl2(), existing::setImageUrl2);
		updateIfNotNull(product.getImageUrl3(), existing::setImageUrl3);
		updateIfNotNull(product.getName(), existing::setName);
		updateIfNotNull(product.getShortDescription(), existing::setShortDescription);
		updateIfNotNull(product.getDescription(), existing::setDescription);
		updateIfNotNull(product.getPrice(), existing::setPrice);
		updateIfNotNull(product.getTags(), existing::setTags);
		return existing;
	}

	private static<T> void updateIfNotNull(T value, Consumer<T> setter) {
		if (value != null) {
			setter.accept(value);
		}
	}

}
