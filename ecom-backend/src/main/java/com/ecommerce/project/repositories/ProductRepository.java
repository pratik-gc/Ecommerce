package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryOrderByPriceAsc(Category category); //JPA will automatically make sense of our
                                                                    // intentions behind making this query that we want
                                                                    //to find category and then order it by price in
                                                                    //ascending order

    List<Product> findByProductNameLikeIgnoreCase(String keyword);
}
