package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails); //JPA will automatically make sense of our
                                                                    // intentions behind making this query that we want
                                                                    //to find category and then order it by price in
                                                                    //ascending order

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);
}
