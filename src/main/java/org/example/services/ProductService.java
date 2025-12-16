package org.example.services;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.client.RestClient;
import org.example.models.Product;

public class ProductService {

    private static final Logger logger = LogManager.getLogger(ProductService.class);
    private static final String PRODUCTS_ENDPOINT = "/public/v2/posts";

    public Response createProduct(Product product) {
        logger.info("Creating product in thread: {}", Thread.currentThread().getId());
        return new RestClient().post(PRODUCTS_ENDPOINT, product);
    }

    public Response getProduct(Long productId) {
        logger.info("Fetching product with ID: {} in thread: {}", productId, Thread.currentThread().getId());
        return new RestClient()
                .addPathParam("productId", String.valueOf(productId))
                .get(PRODUCTS_ENDPOINT + "/{productId}");
    }

    public Response getAllProducts() {
        logger.info("Fetching all products in thread: {}", Thread.currentThread().getId());
        return new RestClient().get(PRODUCTS_ENDPOINT);
    }

    public Response updateProduct(Long productId, Product product) {
        logger.info("Updating product with ID: {} in thread: {}", productId, Thread.currentThread().getId());
        return new RestClient()
                .addPathParam("productId", String.valueOf(productId))
                .put(PRODUCTS_ENDPOINT + "/{productId}", product);
    }

    public Response deleteProduct(Long productId) {
        logger.info("Deleting product with ID: {} in thread: {}", productId, Thread.currentThread().getId());
        return new RestClient()
                .addPathParam("productId", String.valueOf(productId))
                .delete(PRODUCTS_ENDPOINT + "/{productId}");
    }
}
