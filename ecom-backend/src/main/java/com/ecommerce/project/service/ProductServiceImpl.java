package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRespository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;    //path variable will get its value from application.properties file

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        //Check if the new product is already present of not
        List<Product> newProductToBeAdded = productRepository.findAll().stream()
                .filter(product -> product.getProductName().equals(productDTO.getProductName()))
                .toList();
        if (newProductToBeAdded.size()>0){
            throw new APIException("Product Name is already present. Cannot add the product");
        }

        Category category = categoryRespository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId", categoryId));
        Product product = modelMapper.map(productDTO, Product.class);
        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //Check if any products are available or not in the productRepository
        if (productRepository.count() == 0){
            throw new APIException("There are no products available to show currently");
        }

        //Sort is an in-built class used to sort things
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //Pageable is an interface provided by SpringDataJPA and PageRequest is an implemention of Pageable Interface.
        //Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();

        //List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        // Setting Paginaation Metadata for frontend application to consume for rendering
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //First get the category from categoryId
        Category category = categoryRespository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId", categoryId));

        //Sort is an in-built class used to sort things
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //Pageable is an interface provided by SpringDataJPA and PageRequest is an implemention of Pageable Interface.
        //Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = productPage.getContent();


        //======================This logic is also working================================
        //Now extract categoryId from all the products available in productRepository & compare with given categoryId

//        List<Product> products = productRepository.findAll().stream()
//                .filter(product -> product.getCategory().getCategoryId().equals(category.getCategoryId()))
//                .toList();
        //System.out.println(products);
        //==============================END Of Logic====================================

        if(products.isEmpty())
            throw new APIException(category.getCategoryName() + " Category doesn't have any products");

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        // Setting Paginaation Metadata for frontend application to consume for rendering
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        //Check if productRepository has any products present in it or not
        if (productRepository.count() == 0){
            throw new APIException("There are no products available to show currently");
        }

        //Sort is an in-built class used to sort things
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //Pageable is an interface provided by SpringDataJPA and PageRequest is an implemention of Pageable Interface.
        //Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%', pageDetails);
                                                                    // %keyword% =====> Pattern matching in the given String
        List<Product> products = productPage.getContent();

        if(products.isEmpty())
            throw new APIException("Products not found with keyword: " + keyword);

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        // Setting Paginaation Metadata for frontend application to consume for rendering
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;

    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {

        //Get the existing product first from the database
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);

        //Update the product info with newly shared product details in the Requst Body
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        //Save the updated product details into the database
        Product savedProduct = productRepository.save(productFromDb);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product productToBeDeleted = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.delete(productToBeDeleted);

        return modelMapper.map(productToBeDeleted, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        //Get the product from Database
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //Upload the image to server &
        //Get the file name of uploaded image

        //String path = "images/"; //hard-coded path in the codebase for storing the file //Not a good practice
                                 //Therefore move this path into application.properties file
        String fileName = fileService.uploadImage(path, image);

        //Update the new file name to the product
        productFromDb.setImage(fileName);

        //Save the updated product
        Product updatedProduct = productRepository.save(productFromDb);

        //return DTO after mapping product into DTO
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }


}
