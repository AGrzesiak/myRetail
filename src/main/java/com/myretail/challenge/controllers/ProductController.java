package com.myretail.challenge.controllers;

import com.myretail.challenge.models.Product;
import com.myretail.challenge.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{productId}")
    public Product getProduct(@PathVariable("productId") int productId) {
        log.info(">>>getProduct productId: " + productId);
        Product product = productService.getProduct(productId);
        log.info("<<<getProduct product: " + product);
        return product;
    }

    @PutMapping("/products/{productId}")
    public Product updateProduct(@PathVariable("productId") int productId, @RequestBody Product product) {
        log.info(">>>updateProduct productId: " + productId + " product: " + product);
        Product updatedProduct = productService.updateProduct(productId, product);
        log.info("<<<updateProduct productId: " + productId + " updatedProduct: " + updatedProduct);
        return updatedProduct;
    }

}
