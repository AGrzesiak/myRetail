package com.myretail.challenge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.challenge.models.Price;
import com.myretail.challenge.models.Product;
import com.myretail.challenge.models.RedSkyProduct;
import com.myretail.challenge.repositories.PriceRepo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;

@Service
@Transactional
@Log4j2
public class ProductService {

    public static final String REDSKY_URL = "http://redsky.target.com/v2/pdp/tcin/";
    public static final String RESPONSE_ROOT = "product";

    @Autowired
    PriceRepo priceRepo;
    @Autowired
    private CloseableHttpClient httpClient;

    private ProductService(PriceRepo priceRepo, CloseableHttpClient httpClient) {
        this.priceRepo = priceRepo;
        this.httpClient = httpClient;
    }

    public Product getProduct(int productId) {
        log.info(">>>getProduct productId: " + productId);
        String productName = getProductName(productId);
        Set<Price> prices = priceRepo.findByProductId(productId);
        Product product = new Product(productId, productName, prices);
        log.info("<<<getProduct product: " + product);
        return product;
    }

    public Product updateProduct(int productId, Product product) {
        log.info(">>>updateProduct product: " + product);
        Product existingProduct = getProduct(productId);
        if (existingProduct.equals(product)) {
            return product;
        }

        validatePriceProductId(productId, product);

        priceRepo.saveAll(product.getCurrentPrice());
        Product updatedProduct = getProduct(product.getId());
        log.info("<<<updateProduct product: " + updatedProduct);
        return updatedProduct;
    }

    private void validatePriceProductId(int productId, Product product) {
        product.getCurrentPrice().forEach(price -> {
            int priceProductId = price.getProductId();
            if (priceProductId != productId) {
                throw new InvalidUpdateProductRequest("Attempted update of Price with none matching ProductIds. " +
                        " Price: " + price + " attempted update of ProductId: " + productId);
            }
        });
    }

    private String getProductName(int productId) {
        log.info(">>>getProductName productId: " + productId);
        try (CloseableHttpResponse httpResponse = httpClient.execute(new HttpGet(REDSKY_URL + productId))) {
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String productName = parseResponseForName(IOUtils.toString(httpResponse.getEntity().getContent(), Charsets.UTF_8));
                log.info("<<<getProductName productId: " + productId + " productName: " + productName);
                return productName;
            } else if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new ProductNotFoundException("Unable to find product with productId: " + productId + " response: " + httpResponse);
            } else {
                throw new HttpRequestException("Unable to complete request for productId: " + productId + " response: " + httpResponse);
            }
        } catch (IOException e) {
            throw new HttpRequestException("Unable to complete request for productId: " + productId, e);
        }

    }

    private String parseResponseForName(String response) {
        log.info(">>>parseResponseForName");
        ObjectMapper mapper = new ObjectMapper();
        try {
            RedSkyProduct product = mapper.reader().withRootName(RESPONSE_ROOT).forType(RedSkyProduct.class).readValue(response);
            log.info("<<<parseResponseForName");
            return product.getItem().getProductDescription().getTitle();
        } catch (JsonProcessingException e) {
            throw new ResponseParseException("Unable to parse response: " + response, e);
        }
    }
}
