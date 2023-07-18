package com.example.junit.service;

import com.example.junit.TestBase;
import com.example.junit.dao.UserDao;
import com.example.junit.dto.User;
import com.example.junit.extension.ConditionalExtension;
import com.example.junit.extension.PostProcessingExtension;
import com.example.junit.extension.UserServiceParamResolver;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.RepeatedTest.LONG_DISPLAY_NAME;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;

@Tag("fast")

// Рандомая сортировка
//@TestMethodOrder(MethodOrderer.Random.class)

// Сортировка по @Order
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

// Сортировка по алфавитному порядку
//@TestMethodOrder(MethodOrderer.MethodName.class)

// Сортировка по @DisplayName
//@TestMethodOrder(MethodOrderer.DisplayName.class)

@TestInstance(TestInstance.Lifecycle.PER_METHOD)

@ExtendWith({
        UserServiceParamResolver.class,
//        GlobalExtension.class
        PostProcessingExtension.class,
        ConditionalExtension.class,
//        ThrowableExtension.class
        MockitoExtension.class
})

public class UserServiceTest extends TestBase {
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "456");
    @InjectMocks
    private UserService userService;
    @Mock(lenient = true)
    private UserDao userDao;
    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    static void beforeAll() {
        //System.out.println("Before all: ");
    }

    @BeforeEach
    void before() {
        System.out.println("Before each: " + this);
//        lenient().when(userDao.delete(IVAN.getId())).thenReturn(true);
        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
//        Mockito.mock(UserDao.class, withSettings().lenient());

//        this.userDao = Mockito.mock(UserDao.class);
//        this.userDao = Mockito.spy(new UserDao());
//        this.userService = new UserService(userDao);
    }

    @Test
    void throwExceptionIfDatabaseIsNotAvailable() {
        doThrow(RuntimeException.class).when(userDao).delete(IVAN.getId());
        assertThrows(RuntimeException.class, () -> userService.delete(IVAN.getId()));
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
//        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());

        // предпочтительный вариант
//        Mockito.doReturn(true).when(userDao).delete(Mockito.any());

//        Mockito.when(userDao.delete(IVAN.getId())).thenReturn(true);

        boolean deleteResult = userService.delete(IVAN.getId());

//        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(userDao, Mockito.atLeast(1)).delete(argumentCaptor.capture());
//        Mockito.verifyNoInteractions();

        assertThat(argumentCaptor.getValue()).isEqualTo(IVAN.getId());

//        Mockito.reset(userDao);

        assertThat(deleteResult).isTrue();
    }

    @Order(1)
//    @DisplayName("AAA")
    @Test
    void usersEmptyIfNoUserAdded() {
//        if (true) {
//            throw new RuntimeException();
//        }

        //System.out.println("Test 1: " + this);
        List<User> users = userService.getAll();

        //junit
        assertTrue(users.isEmpty());

        //hamcrest
        MatcherAssert.assertThat(users, empty());
    }

    @Test
    void usersSizeIfUsersAdded() {
        // given
        // when
        // then

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
    @Disabled("flaky, need to see")
    @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
    void checkLoginFunctionalityPerformance() {
//        Optional<User> result = assertTimeout(Duration.ofMillis(200L), () -> {
//            Thread.sleep(300L);
//            return userService.login("dummy", IVAN.getPassword());
//        });

        System.out.println(Thread.currentThread().getName());
        Optional<User> result = assertTimeoutPreemptively(Duration.ofMillis(200L), () -> {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(300L);
            return userService.login("dummy", IVAN.getPassword());
        });
    }

    @Order(2)
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

    @AfterEach
    void after() {
        //System.out.println("After each: " + this);
    }

    @AfterAll
    static void afterAll() {
        //System.out.println("After all: ");
    }

    @Nested
    @DisplayName("Test login")
    @Tag("login")
    class LoginTest {
        @Test

        // не выполнять тест
//        @Disabled("flaky, need to see")

        @Tag("login")
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getPassword(), "dummy");

            //junit
            assertTrue(maybeUser.isEmpty());
        }

        // Запустить несколько раз
        @RepeatedTest(value = 5, name = LONG_DISPLAY_NAME)
        @Disabled("flaky, need to see")
        @Test
        @Tag("login")
//        void loginFailIfUserDoesNotExist(RepetitionInfo repetitionInfo) {
        void loginFailIfUserDoesNotExist(RepetitionInfo repetitionInfo) {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());

            //junit
            assertTrue(maybeUser.isEmpty());
        }

        @Test
        @Tag("login")
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

        @ParameterizedTest(name = "Тесты на логин {index}")

//        @ArgumentsSource()

//        @NullSource

//        @EmptySource

//        @NullAndEmptySource

//        @ValueSource(strings = {
//                "Ivan", "Petr"
//        })

//        @EnumSource

        @MethodSource("com.example.junit.service.UserServiceTest#getArgumentsForLoginTest")

//        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)


//        @CsvSource({
//                "Ivan,123",
//                "Petr, 456"
//        })
//        void loginParametrizedTest(String username, String password) {

            // для @MethodSource
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);

            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }
    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "456", Optional.of(PETR)),
                Arguments.of("Petr", "dummy", Optional.empty()),
                Arguments.of("dummy", "123", Optional.empty())
        );
    }
}