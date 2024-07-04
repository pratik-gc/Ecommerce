package com.ecommerce.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

//In the context of JPA, an Entity represents a table in a relational database
//@Entity
//@Entity(name = "<Your_Customized_Table_Name_Here>")
@Entity(name = "categories")
@Data //A shortcut for @ToString, @EqualsAndHashCode, @Getter on all fields, and @Setter on all non-final fields, and @RequiredArgsConstructor!
@NoArgsConstructor
@AllArgsConstructor
//@Getter
//@Setter
public class Category {
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


    //@Getter & @Setter Lombok annotations doesn't work in my machine. Don't know why!!!!!!!!!!
    //Therefore, creating Getters and Setters explicitly.
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public @NotBlank @Size(min = 5, message = "Category Name must contain atleast 5 characters") String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NotBlank @Size(min = 5, message = "Category Name must contain atleast 5 characters") String categoryName) {
        this.categoryName = categoryName;
    }
}
