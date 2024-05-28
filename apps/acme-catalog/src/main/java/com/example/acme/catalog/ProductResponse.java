package com.example.acme.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

public class ProductResponse {

	private String id;
	private String imageUrl1;
	private String imageUrl2;
	private String imageUrl3;
	private String name;
	private String shortDescription;
	private String description;
	private Double price;
	private List<String> tags = new ArrayList<>();

	public ProductResponse() {
	}

	public ProductResponse(Product product) {
		id = product.getId();
		price = product.getPrice();
		description = product.getDescription();
		name = product.getName();
		imageUrl1 = product.getImageUrl1();
		imageUrl2 = product.getImageUrl2();
		imageUrl3 = product.getImageUrl3();
		shortDescription = product.getShortDescription();

		if (StringUtils.hasText(product.getTags())) {
			tags = Arrays.asList(product.getTags().split(","));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImageUrl1() {
		return imageUrl1;
	}

	public void setImageUrl1(String imageUrl1) {
		this.imageUrl1 = imageUrl1;
	}

	public String getImageUrl2() {
		return imageUrl2;
	}

	public void setImageUrl2(String imageUrl2) {
		this.imageUrl2 = imageUrl2;
	}

	public String getImageUrl3() {
		return imageUrl3;
	}

	public void setImageUrl3(String imageUrl3) {
		this.imageUrl3 = imageUrl3;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
