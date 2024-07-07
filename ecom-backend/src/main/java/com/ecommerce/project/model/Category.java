package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

//In the context of JPA, an Entity represents a table in a relational database
//@Entity
//@Entity(name = "<Your_Customized_Table_Name_Here>")
@Entity(name = "categories")
@Data //A shortcut for @ToString, @EqualsAndHashCode, @Getter on all fields, and @Setter on all non-final fields, and @RequiredArgsConstructor!
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Category {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank
    //@Min(3)
    //@Max(10)
    //@Size(min = 3, max = 10)
    //@Size(min = 5)
    @Size(min = 5, message = "Category Name must contain atleast 5 characters")
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;


    public @NotBlank @Size(min = 5, message = "Category Name must contain atleast 5 characters") String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NotBlank @Size(min = 5, message = "Category Name must contain atleast 5 characters") String categoryName) {
        this.categoryName = categoryName;
    }
}
