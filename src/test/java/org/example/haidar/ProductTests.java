package org.example.haidar;

import io.restassured.response.Response;
import org.example.models.Product;
import org.example.services.ProductService;
import org.example.utils.ExtentManager;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ProductTests extends BaseTest {
    private ProductService productService;
    private final ThreadLocal<Product> testProduct = new ThreadLocal<>();

    @BeforeClass
    public void setupClass() {
        super.setupClass();
        productService = new ProductService();
    }

    @Test(priority = 1, description = "Create a new product")
    public void testCreateProduct() {
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());

        Product product = Product.builder()
                .userId(12345L)
                .title("Test Product " + Thread.currentThread().getId())
                .body("This is a test product description")
                .build();

        testProduct.set(product);

        ExtentManager.getTest().info("Creating new product: " + product.getTitle());

        Response response = productService.createProduct(product);

        Assert.assertEquals(response.getStatusCode(), 201, "Status code mismatch");

        Product createdProduct = response.as(Product.class);
        product.setId(createdProduct.getId());

        Assert.assertNotNull(createdProduct.getId(), "Product ID should not be null");

        ExtentManager.getTest().pass("Product created successfully with ID: " + createdProduct.getId());

        testProduct.set(product);
    }

    @Test(priority = 2, description = "Get product by ID", dependsOnMethods = "testCreateProduct")
    public void testGetProduct() {
        Product product = testProduct.get();
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Fetching product with ID: " + product.getId());

        Response response = productService.getProduct(product.getId());

        Assert.assertEquals(response.getStatusCode(), 200);

        Product fetchedProduct = response.as(Product.class);
        Assert.assertEquals(fetchedProduct.getId(), product.getId());

        ExtentManager.getTest().pass("Product fetched successfully");
    }

    @Test(priority = 3, description = "Update product", dependsOnMethods = "testCreateProduct")
    public void testUpdateProduct() {
        Product product = testProduct.get();
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Updating product with ID: " + product.getId());

        product.setTitle("Updated Product " + Thread.currentThread().getId());
        product.setBody("This is an updated product description");

        Response response = productService.updateProduct(product.getId(), product);

        Assert.assertEquals(response.getStatusCode(), 200);

        Product updatedProduct = response.as(Product.class);
        Assert.assertTrue(updatedProduct.getTitle().contains("Updated"));

        ExtentManager.getTest().pass("Product updated successfully");
    }

    @Test(priority = 4, description = "Delete product", dependsOnMethods = {"testGetProduct", "testUpdateProduct"})
    public void testDeleteProduct() {
        Product product = testProduct.get();
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Deleting product with ID: " + product.getId());

        Response response = productService.deleteProduct(product.getId());

        Assert.assertEquals(response.getStatusCode(), 204);

        ExtentManager.getTest().pass("Product deleted successfully");

        // Cleanup ThreadLocal
        testProduct.remove();
    }

    @Test(priority = 5, description = "Get all products - Independent test")
    public void testGetAllProducts() {
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Fetching all products");

        Response response = productService.getAllProducts();

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.jsonPath().getList("$").size() > 0);

        ExtentManager.getTest().pass("All products fetched successfully. Count: " +
                response.jsonPath().getList("$").size());
    }

    @Test(priority = 6, description = "Verify product schema validation")
    public void testProductSchemaValidation() {
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Validating product response schema");

        Response response = productService.getAllProducts();

        Assert.assertEquals(response.getStatusCode(), 200);

        // Validate response structure
        Assert.assertNotNull(response.jsonPath().getList("$"));

        // Validate first product has required fields
        if (response.jsonPath().getList("$").size() > 0) {
            Assert.assertNotNull(response.jsonPath().get("[0].id"));
            Assert.assertNotNull(response.jsonPath().get("[0].user_id"));
            Assert.assertNotNull(response.jsonPath().get("[0].title"));
            Assert.assertNotNull(response.jsonPath().get("[0].body"));
        }

        ExtentManager.getTest().pass("Product schema validation passed");
    }
}
