package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "Product Name must contain atleast 3 characters")
    private String productName;
    private String image;

    @NotBlank
    @Size(min = 5, message = "Product Description must contain atleast 5 characters")
    private String description;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


//    public Product() {
//    }

//    public Product(Long productId, String productName, String description, Integer quantity, double price, double specialPrice, Category category) {
//        this.productId = productId;
//        this.productName = productName;
//        this.description = description;
//        this.quantity = quantity;
//        this.price = price;
//        this.specialPrice = specialPrice;
//        this.category = category;
//    }
//
//    public Long getProductId() {
//        return productId;
//    }

//    public void setProductId(Long productId) {
//        this.productId = productId;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public Integer getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(Integer quantity) {
//        this.quantity = quantity;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public double getSpecialPrice() {
//        return specialPrice;
//    }
//
//    public void setSpecialPrice(double specialPrice) {
//        this.specialPrice = specialPrice;
//    }
//
//    public Category getCategory() {
//        return category;
//    }
//
//    public void setCategory(Category category) {
//        this.category = category;
//    }
}
