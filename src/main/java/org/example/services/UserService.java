package org.example.services;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.client.RestClient;
import org.example.models.User;

public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);
    private static final String USERS_ENDPOINT = "/public/v2/users";

    public Response createUser(User user) {
        logger.info("Creating user: {} in thread: {}", user.getName(), Thread.currentThread().getId());
        return new RestClient().post(USERS_ENDPOINT, user);
    }

    public Response getUser(Long userId) {
        logger.info("Fetching user with ID: {} in thread: {}", userId, Thread.currentThread().getId());
        return new RestClient()
                .addPathParam("userId", String.valueOf(userId))
                .get(USERS_ENDPOINT + "/{userId}");
    }

    public Response getAllUsers() {
        logger.info("Fetching all users in thread: {}", Thread.currentThread().getId());
        return new RestClient().get(USERS_ENDPOINT);
    }

    public Response updateUser(Long userId, User user) {
        logger.info("Updating user with ID: {} in thread: {}", userId, Thread.currentThread().getId());
        return new RestClient()
                .addPathParam("userId", String.valueOf(userId))
                .put(USERS_ENDPOINT + "/{userId}", user);
    }

    public Response deleteUser(Long userId) {
        logger.info("Deleting user with ID: {} in thread: {}", userId, Thread.currentThread().getId());
        return new RestClient()
                .addPathParam("userId", String.valueOf(userId))
                .delete(USERS_ENDPOINT + "/{userId}");
    }

    public Response searchUsersByName(String name) {
        logger.info("Searching users by name: {} in thread: {}", name, Thread.currentThread().getId());
        return new RestClient()
                .addQueryParam("name", name)
                .get(USERS_ENDPOINT);
    }

    public Response searchUsersByStatus(String status) {
        logger.info("Searching users by status: {} in thread: {}", status, Thread.currentThread().getId());
        return new RestClient()
                .addQueryParam("status", status)
                .get(USERS_ENDPOINT);
    }
}
