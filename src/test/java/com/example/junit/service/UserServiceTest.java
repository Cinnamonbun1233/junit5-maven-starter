package com.example.junit.service;

import com.example.junit.dto.User;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceTest {
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "456");
    private UserService userService;

    @BeforeAll
    static void beforeAll() {
        //System.out.println("Before all: ");
    }

    @BeforeEach
    void before() {
        //System.out.println("Before each: " + this);
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNoUserAdded() {
        //System.out.println("Test 1: " + this);
        List<User> users = userService.getAll();

        //junit
        assertTrue(users.isEmpty());

        //hamcrest
        MatcherAssert.assertThat(users, empty());
    }

    @Test
    void usersSizeIfUsersAdded() {
        //System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        List<User> users = userService.getAll();

        //assertj
        assertThat(users).hasSize(2);

        //junit
        //assertEquals(2, users.size());
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUserName(), IVAN.getPassword());

        //assertj
        assertThat(maybeUser).isPresent();
        maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));

        //junit
        //assertTrue(maybeUser.isPresent());
        //maybeUser.ifPresent(user -> assertEquals(IVAN, user));
    }

    @Test
    void throwExceptionIfUsernameOrPasswordIsNull() {
//        try {
//            userService.login(null, "dummy");
//            fail("login should throw exception on null username");
//        } catch (IllegalArgumentException exception) {
//            assertTrue(true);
//        }

        assertAll(
                () -> {
                    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                            () -> userService.login(null, "dummy"));
                    assertThat(exception.getMessage()).isEqualTo("username or password is null");
                },
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
        );

//        assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy"));
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, User> users = userService.getAllConvertedById();

        //assertj
        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );

        //hamcrest
        //MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId()));
    }

    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getPassword(), "dummy");

        //junit
        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void loginFailIfUserDoesNotExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());

        //junit
        assertTrue(maybeUser.isEmpty());
    }

    @AfterEach
    void after() {
        //System.out.println("After each: " + this);
    }

    @AfterAll
    static void afterAll() {
        //System.out.println("After all: ");
    }
}