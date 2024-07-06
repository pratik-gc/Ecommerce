package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRespository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
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
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByCategory(Long categoryId) {
        //First get the category from categoryId
        Category category = categoryRespository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId", categoryId));

        //======================This logic is also working================================
        //Now extract categoryId from all the products available in productRepository & compare with given categoryId

//        List<Product> products = productRepository.findAll().stream()
//                .filter(product -> product.getCategory().getCategoryId().equals(category.getCategoryId()))
//                .toList();
        //System.out.println(products);
        //==============================END Of Logic====================================

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%');
                                                        // %keyword% =====> Pattern matching in the given String
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
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

        String path = "images/";
        String fileName = uploadImage(path, image);

        //Update the new file name to the product
        productFromDb.setImage(fileName);

        //Save the updated product
        Product updatedProduct = productRepository.save(productFromDb);

        //return DTO after mapping product into DTO
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }

    private String uploadImage(String path, MultipartFile imageFile) throws IOException {
        //Get the file name of original file
        String originalFileName = imageFile.getOriginalFilename(); //.getOriginalFileName() will give us entire name
                                                                    //with its extension
                                            //If we use .getName() here, we will get StringIndexOutOfBoundException

        //Rename the file uniquely i.e. Generate a Unique file name
        String randomId = UUID.randomUUID().toString(); //UUID is an in-build class
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
                    //e.g.: hills.jpg (originalFileName) ===> 4545 (randomId) ===> 4545.jpg (newFileName)
        String filePath = path + File.separator + fileName;

        //Check if path exists and create
        File folder = new File(path); //creating a File object from the received path
        if (!folder.exists())
            folder.mkdir();

        //Upload to the server
        Files.copy(imageFile.getInputStream(), Paths.get(filePath));

        //return the file name
        return fileName;
    }
}
