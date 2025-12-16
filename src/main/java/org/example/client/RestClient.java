package org.example.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

public class RestClient {

    private static final Logger logger = LogManager.getLogger(RestClient.class);

    // ThreadLocal to store request for current thread
    private final ThreadLocal<RequestSpecification> request = new ThreadLocal<>();

    public RestClient() {
        // Initialize request with thread-safe RequestSpec
        request.set(RestAssured.given().spec(SpecBuilder.getRequestSpec()));
    }

    /*
     * Get current thread's request specification
     */
    private RequestSpecification getRequest() {
        if (request.get() == null) {
            request.set(RestAssured.given().spec(SpecBuilder.getRequestSpec()));
        }
        return request.get();
    }

    public RestClient addHeader(String key, String value) {
        getRequest().header(key, value);
        return this;
    }

    public RestClient addHeaders(Map<String, String> headers) {
        getRequest().headers(headers);
        return this;
    }

    public RestClient addQueryParam(String key, String value) {
        getRequest().queryParam(key, value);
        return this;
    }

    public RestClient addQueryParams(Map<String, String> params) {
        getRequest().queryParams(params);
        return this;
    }

    public RestClient addPathParam(String key, String value) {
        getRequest().pathParam(key, value);
        return this;
    }

    public RestClient addPathParams(Map<String, String> params) {
        getRequest().pathParams(params);
        return this;
    }

    public RestClient setContentType(String contentType) {
        getRequest().contentType(contentType);
        return this;
    }

    public Response get(String endpoint) {
        logger.info("GET Request to: {} by thread: {}", endpoint, Thread.currentThread().getId());
        Response response = getRequest()
                .when()
                .get(endpoint)
                .then()
                .spec(SpecBuilder.getResponseSpec())
                .extract()
                .response();

        logResponse(response);
        cleanupRequest();
        return response;
    }

    public Response post(String endpoint, Object body) {
        logger.info("POST Request to: {} by thread: {}", endpoint, Thread.currentThread().getId());
        Response response = getRequest()
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .spec(SpecBuilder.getResponseSpec())
                .extract()
                .response();

        logResponse(response);
        cleanupRequest();
        return response;
    }

    public Response put(String endpoint, Object body) {
        logger.info("PUT Request to: {} by thread: {}", endpoint, Thread.currentThread().getId());
        Response response = getRequest()
                .body(body)
                .when()
                .put(endpoint)
                .then()
                .spec(SpecBuilder.getResponseSpec())
                .extract()
                .response();

        logResponse(response);
        cleanupRequest();
        return response;
    }

    public Response patch(String endpoint, Object body) {
        logger.info("PATCH Request to: {} by thread: {}", endpoint, Thread.currentThread().getId());
        Response response = getRequest()
                .body(body)
                .when()
                .patch(endpoint)
                .then()
                .spec(SpecBuilder.getResponseSpec())
                .extract()
                .response();

        logResponse(response);
        cleanupRequest();
        return response;
    }

    public Response delete(String endpoint) {
        logger.info("DELETE Request to: {} by thread: {}", endpoint, Thread.currentThread().getId());
        Response response = getRequest()
                .when()
                .delete(endpoint)
                .then()
                .spec(SpecBuilder.getResponseSpec())
                .extract()
                .response();

        logResponse(response);
        cleanupRequest();
        return response;
    }

    /*
     * POST with custom response spec
     */
    public Response postWithExpectedStatus(String endpoint, Object body, int expectedStatus) {
        logger.info("POST Request with expected status {} to: {} by thread: {}",
                expectedStatus, endpoint, Thread.currentThread().getId());

        Response response = getRequest()
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .spec(SpecBuilder.getResponseSpecWithStatus(expectedStatus))
                .extract()
                .response();

        logResponse(response);
        cleanupRequest();
        return response;
    }

    private void logResponse(Response response) {
        logger.info("Response Status Code: {} | Thread: {}",
                response.getStatusCode(), Thread.currentThread().getId());
        logger.info("Response Time: {} ms | Thread: {}",
                response.getTime(), Thread.currentThread().getId());
    }

    /*
     * Cleanup ThreadLocal request after use
     */
    private void cleanupRequest() {
        request.remove();
    }
}
