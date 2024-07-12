package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);
    
    @Transactional//It makes sure that the method runs within the Transactional context
                   //i.e. if any part fails, the entire operation will be rolled back, ensuring data integrity
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}
