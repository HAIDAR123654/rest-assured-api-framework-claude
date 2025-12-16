package org.example.haidar;

import io.restassured.response.Response;
import org.example.models.User;
import org.example.services.UserService;
import org.example.utils.ExtentManager;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserTests extends BaseTest {

    private UserService userService;
    private final ThreadLocal<User> testUser = new ThreadLocal<>();

    @BeforeClass
    public void setupClass() {
        super.setupClass();
        userService = new UserService();
    }

    @Test(priority = 1, description = "Create a new user")
    public void testCreateUser() {

        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());

        User user = User.builder()
                .name("John Doe " + Thread.currentThread().getId())
                .email("john.doe" + System.currentTimeMillis() + "@test.com")
                .gender("male")
                .status("active")
                .build();

        testUser.set(user);

        ExtentManager.getTest().info("Creating new user: " + user.getName());

        Response response = userService.createUser(testUser.get());

        Assert.assertEquals(response.getStatusCode(), 201, "Status code mismatch");

        User createdUser = response.as(User.class);
        testUser.get().setId(createdUser.getId());

        Assert.assertNotNull(createdUser.getId(), "User ID should not be null");
        Assert.assertEquals(createdUser.getName(), user.getName());

        ExtentManager.getTest().pass("User created successfully with ID: " + createdUser.getId());

        // Store for dependent tests
        testUser.set(user);
    }

    @Test(priority = 2, description = "Get user by ID", dependsOnMethods = "testCreateUser", enabled = false)
    public void testGetUser() {

        User user = testUser.get();
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Fetching user with ID: " + user.getId());

        Response response = userService.getUser(user.getId());

        Assert.assertEquals(response.getStatusCode(), 200);

        User fetchedUser = response.as(User.class);
        Assert.assertEquals(fetchedUser.getId(), user.getId());
        Assert.assertEquals(fetchedUser.getEmail(), user.getEmail());

        ExtentManager.getTest().pass("User fetched successfully");
    }

    @Test(priority = 3, description = "Update user", dependsOnMethods = "testCreateUser", enabled = false)
    public void testUpdateUser() {
        User user = testUser.get();
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Updating user with ID: " + user.getId());

        user.setName("John Doe Updated " + Thread.currentThread().getId());
        Response response = userService.updateUser(user.getId(), user);

        Assert.assertEquals(response.getStatusCode(), 200);

        User updatedUser = response.as(User.class);
        Assert.assertTrue(updatedUser.getName().contains("Updated"));

        ExtentManager.getTest().pass("User updated successfully");
    }

    @Test(priority = 4, description = "Delete user", dependsOnMethods = {"testGetUser", "testUpdateUser"}, enabled = false)
    public void testDeleteUser() {
        User user = testUser.get();
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Deleting user with ID: " + user.getId());

        Response response = userService.deleteUser(user.getId());

        Assert.assertEquals(response.getStatusCode(), 204);

        ExtentManager.getTest().pass("User deleted successfully");

        // Cleanup ThreadLocal
        testUser.remove();
    }

    @Test(priority = 5, description = "Get all users - Independent test for parallel execution", enabled = false)
    public void testGetAllUsers() {
        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Fetching all users");

        Response response = userService.getAllUsers();

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertFalse(response.jsonPath().getList("$").isEmpty());

        ExtentManager.getTest().pass("All users fetched successfully. Count: " +
                response.jsonPath().getList("$").size());
    }

    @Test(priority = 6, description = "Search users by status - Independent test", enabled = false)
    public void testSearchUsersByStatus() {

        ExtentManager.getTest().info("Thread ID: " + Thread.currentThread().getId());
        ExtentManager.getTest().info("Searching users with status: active");

        Response response = userService.searchUsersByStatus("active");

        Assert.assertEquals(response.getStatusCode(), 200);

        ExtentManager.getTest().pass("Users search by status completed successfully");
    }
}
