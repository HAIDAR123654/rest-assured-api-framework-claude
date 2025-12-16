package org.example.client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.*;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.utils.ConfigReader;

public class SpecBuilder {

    private static final Logger logger = LogManager.getLogger(SpecBuilder.class);

    // ThreadLocal to ensure thread safety during parallel execution
    private static final ThreadLocal<RequestSpecification> requestSpec = new ThreadLocal<>();
    private static final ThreadLocal<ResponseSpecification> responseSpec = new ThreadLocal<>();

    /*
     * Get RequestSpecification for current thread
     */
    public static RequestSpecification getRequestSpec() {
        if (requestSpec.get() == null) {
            requestSpec.set(createRequestSpec());
        }
        return requestSpec.get();

    }

    /*
     * Get ResponseSpecification for current thread
     */
    public static ResponseSpecification getResponseSpec() {
        if (responseSpec.get() == null) {
            responseSpec.set(createResponseSpec());
        }
        return responseSpec.get();
    }

    /*
     * Create RequestSpecification with common configurations
     */
    private static RequestSpecification createRequestSpec() {
        logger.info("Creating RequestSpecification for thread: {}", Thread.currentThread().getId());

        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getInstance().getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + ConfigReader.getInstance().getAuthToken())
                .log(LogDetail.ALL)
                .build();

        return spec;
    }

    /*
     * Create ResponseSpecification with common validations
     */
    private static ResponseSpecification createResponseSpec() {
        logger.info("Creating ResponseSpecification for thread: {}", Thread.currentThread().getId());

        ResponseSpecification spec = new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();

        return spec;
    }

    /*
     * Create RequestSpec with custom auth token
     */
    public static RequestSpecification getRequestSpecWithToken(String token) {
        logger.info("Creating RequestSpecification with custom token for thread: {}",
                Thread.currentThread().getId());

        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getInstance().getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .log(LogDetail.ALL)
                .build();

        requestSpec.set(spec);
        return spec;
    }

    /*
     * Create RequestSpec without authentication
     */
    public static RequestSpecification getRequestSpecWithoutAuth() {
        logger.info("Creating RequestSpecification without auth for thread: {}",
                Thread.currentThread().getId());

        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getInstance().getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        requestSpec.set(spec);
        return spec;
    }

    /*
     * Create ResponseSpec with expected status code
     */
    public static ResponseSpecification getResponseSpecWithStatus(int statusCode) {
        logger.info("Creating ResponseSpecification with status {} for thread: {}",
                statusCode, Thread.currentThread().getId());

        ResponseSpecification spec = new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(LogDetail.ALL)
                .build();

        responseSpec.set(spec);
        return spec;
    }

    /*
     * Clean up ThreadLocal variables to prevent memory leaks
     */
    public static void removeThreadLocalSpecs() {
        logger.info("Removing ThreadLocal specs for thread: {}", Thread.currentThread().getId());
        requestSpec.remove();
        responseSpec.remove();
    }

    /*
     * Reset specs for new test execution
     */
    public static void resetSpecs() {
        logger.info("Resetting specs for thread: {}", Thread.currentThread().getId());
        requestSpec.set(createRequestSpec());
        responseSpec.set(createResponseSpec());
    }
}
