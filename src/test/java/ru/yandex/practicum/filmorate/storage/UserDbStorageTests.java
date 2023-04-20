package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTests {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    public void testFindUserById() {
        User user = userStorage.add(new User("gmail@mail.ru", "log",
                "Nike", LocalDate.of(1999, 7, 21)));
        Optional<User> userOptional = userStorage.getById(user.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", 33)
                );
    }

    @Test
    public void testFindUserNameIsEmptyById() {
        User user = userStorage.add(new User("gmail@mail.ru", "log",
                "", LocalDate.of(1999, 7, 21)));
        Optional<User> userOptional = userStorage.getById(user.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", user.getId())
                );
    }

    @Test
    public void testGetUsers() {
        List<User> users = userStorage.getAll();

        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    public void testCreateUser() {
        User user = userStorage.add(new User("gmail@mail.ru", "log",
                "Nike", LocalDate.of(1999, 7, 21)));

        assertThat(user).hasFieldOrPropertyWithValue("id", 32)
                .hasFieldOrPropertyWithValue("name", "Nike")
                .hasFieldOrPropertyWithValue("email", "gmail@mail.ru")
                .hasFieldOrPropertyWithValue("login", "log")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 7, 21));
    }

    @Test
    public void testUpdateUser() {
        User user = userStorage.add(new User("gmail@mail.ru", "log",
                "Nike", LocalDate.of(1999, 7, 21)));
        Optional<User> userOptional = userStorage.update(new User(user.getId(), "mail@mail.ru", "LOGIN",
                "Make", LocalDate.of(2000, 1, 20)));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("id", user.getId())
                                .hasFieldOrPropertyWithValue("name", "Make")
                                .hasFieldOrPropertyWithValue("email", "mail@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "LOGIN")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 1, 20))
                );
    }

    @Test
    public void testGetFriends() {
        List<User> userOptional = userStorage.getFriends(1);

        assertThat(userOptional.size()).isEqualTo(0);
    }

    @Test
    public void testGetCommonFriend() {
        User user1 = userStorage.add(new User("gmail@mail.ru", "log",
                "Nike", LocalDate.of(1999, 7, 21)));
        User user2 = userStorage.add(new User("gmail@mail.ru", "log",
                "Nike", LocalDate.of(1999, 7, 21)));
        User user3 = userStorage.add(new User("gmail@mail.ru", "log",
                "Nike", LocalDate.of(1999, 7, 21)));
        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user3.getId());
        List<User> userOptional = userStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(userOptional.size()).isEqualTo(1);
        assertThat(userOptional.get(0).getId()).isEqualTo(user3.getId());
    }

    @Test
    public void testRemoveAndGetFriend() {
        userStorage.removeFriend(2, 3);
        List<User> userOptional = userStorage.getFriends(2);

        assertThat(userOptional.size()).isEqualTo(0);
    }

    @Test
    public void testRemoveUser() {
        User user = userStorage.add(new User("gmail@mail.ru", "log",
                "Nike", LocalDate.of(1999, 7, 21)));
        userStorage.remove(user.getId());
        Optional<User> userOptional = userStorage.getById(user.getId());

        assertThat(userOptional).isEmpty();
    }
}
