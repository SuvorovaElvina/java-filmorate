package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.throwable.IncorrectCountException;
import ru.yandex.practicum.filmorate.throwable.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;

    @Test
    void createUser() {
        User user = userService.createUser(new User("gmail@mail.ru", "log",
                "Nike", LocalDate.of(1999, 7, 21)));

        assertThat(user).hasFieldOrPropertyWithValue("id", 4)
                .hasFieldOrPropertyWithValue("name", "Nike")
                .hasFieldOrPropertyWithValue("email", "gmail@mail.ru")
                .hasFieldOrPropertyWithValue("login", "log")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 7, 21));
    }

    @Test
    void createUserNameIsBlank() {
        User user = userService.createUser(new User("gmail@mail.ru", "log",
                "", LocalDate.of(1999, 7, 21)));

        assertThat(user).hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("name", "log")
                .hasFieldOrPropertyWithValue("email", "gmail@mail.ru")
                .hasFieldOrPropertyWithValue("login", "log")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 7, 21));
    }

    @Test
    void createUserNameIsNull() {
        User user = userService.createUser(new User("gmail@mail.ru", "log",
                null, LocalDate.of(1999, 7, 21)));

        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "log")
                .hasFieldOrPropertyWithValue("email", "gmail@mail.ru")
                .hasFieldOrPropertyWithValue("login", "log")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 7, 21));
    }

    @Test
    void updateUser() {
        User user = userService.updateUser(new User(1, "mail@mail.ru", "LOGIN",
                "Make", LocalDate.of(2000, 1, 20)));

        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Make")
                .hasFieldOrPropertyWithValue("email", "mail@mail.ru")
                .hasFieldOrPropertyWithValue("login", "LOGIN")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 1, 20));
    }

    @Test
    void updateUserUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            userService.updateUser(new User(9999, "mail@mail.ru", "LOGIN",
                    "Make", LocalDate.of(2000, 1, 20)));
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void getUsers() {
        List<User> users = userService.getUsers();

        assertThat(users.size()).isEqualTo(5);
    }

    @Test
    public void findUserById() {
        User user = userService.getUser(2);

        assertThat(user).hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("name", "log")
                .hasFieldOrPropertyWithValue("email", "gmail@mail.ru")
                .hasFieldOrPropertyWithValue("login", "log")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 7, 21));
    }

    @Test
    public void findUserByIdUnknown() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            userService.getUser(9999);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    public void findUserByIdNegative() {
        Throwable thrown = assertThrows(IncorrectCountException.class, () -> {
            userService.getUser(-1);
        });

        Assertions.assertNotNull(thrown.getMessage());
    }

    @Test
    void addFriend() {
        userService.addFriend(1,2);
        assertThat(userService.getFriends(1).size()).isEqualTo(1);
        userService.removeFriend(1,2);
    }

    @Test
    void removeFriend() {
        userService.addFriend(1,5);
        userService.removeFriend(1,5);

        assertThat(userService.getFriends(1).size()).isEqualTo(0);
    }

    @Test
    void getFriends() {
        assertThat(userService.getFriends(1).size()).isEqualTo(0);

        userService.addFriend(1,2);

        assertThat(userService.getFriends(1).size()).isEqualTo(1);
        userService.removeFriend(1,2);
    }

    @Test
    void getCommonFriends() {
        userService.createUser(new User("gmail@mail.ru", "log",
                null, LocalDate.of(1999, 7, 21)));
        userService.addFriend(1,5);
        userService.addFriend(2,5);

        assertThat(userService.getCommonFriends(1,2).get(0).getId()).isEqualTo(5);
        userService.removeFriend(1,5);
        userService.removeFriend(2,5);
    }
}