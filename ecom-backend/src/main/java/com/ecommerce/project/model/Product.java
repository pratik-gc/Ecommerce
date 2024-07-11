package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString
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
    //private int quantity = 0;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    //==============For Seller of the Product==================
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;
    //=====================END=================================

    @OneToMany(mappedBy = "product",
                cascade = {CascadeType.MERGE, CascadeType.PERSIST},
                fetch = FetchType.EAGER)
    private List<CartItem> products = new ArrayList<>();
}
