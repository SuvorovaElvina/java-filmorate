package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.getById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindUserNameIsEmptyById() {
        Optional<User> userOptional = userStorage.getById(2);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2)
                );
    }

    @Test
    public void testGetUsers() {
        List<User> users = userStorage.getAll();

        assertThat(users.size()).isEqualTo(3);
    }

    @Test
    public void testCreateUser() {
        Optional<User> userOptional = userStorage.add(new User("gmail@mail.ru", "log",
                "Nike", LocalDate.of(1999, 7, 21)));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Nike")
                                .hasFieldOrPropertyWithValue("email", "gmail@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "log")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 7, 21))

                );
    }

    @Test
    public void testUpdateUser() {
        Optional<User> userOptional = userStorage.update(new User(1, "mail@mail.ru", "LOGIN",
                "Make", LocalDate.of(2000, 1, 20)));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
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
        userStorage.addFriend(1, 3);
        userStorage.addFriend(2, 3);
        List<User> userOptional = userStorage.getCommonFriends(1, 2);

        assertThat(userOptional.size()).isEqualTo(1);
        assertThat(userOptional.get(0).getId()).isEqualTo(3);
    }

    @Test
    public void testRemoveAndGetFriend() {
        userStorage.removeFriend(2, 3);
        List<User> userOptional = userStorage.getFriends(2);

        assertThat(userOptional.size()).isEqualTo(0);
    }

    @Test
    public void testRemoveUser() {
        userStorage.remove(1);
        Optional<User> userOptional = userStorage.getById(1);

        assertThat(userOptional).isEmpty();
    }
}
