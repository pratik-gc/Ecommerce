package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO { //representing a single cart item
    private Long cartItemId;
    private CartDTO cart;
    private ProductDTO productDTO;
    //private int quantity;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
