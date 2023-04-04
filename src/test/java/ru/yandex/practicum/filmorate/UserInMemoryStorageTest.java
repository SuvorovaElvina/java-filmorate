package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserInMemoryStorageTest {
    private static final Validator validator;
    private static final UserStorage users = new InMemoryUserStorage();
    private static final UserService service = new UserService(users);

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void createNewUser() {
        User user = new User("Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        service.createUser(user);

        assertNotNull(service.getUser(4), "Не создаёт пользователь - null");
        assertEquals(service.getUser(4).toString(), user.toString(), "Создаёт неправильного пользователя");
    }

    @Test
    public void updateUser() {
        User user = new User("Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        user.setId(1);
        service.updateUser(user);

        assertNotNull(service.getUser(1), "Не обновляет пользователя - null");
        assertEquals(service.getUser(user.getId()).getId(), user.getId(), "Обнавляет не того пользователя");
        assertEquals(service.getUser(user.getId()), user, "Не обновляет пользователя");
    }

    @Test
    public void validateLoginIsBlank() {
        User userLogin = new User("Test@mail.ru", "", "name", LocalDate.of(2023, 1, 12));
        User userLogin1 = new User("Test@mail.ru", null, "name", LocalDate.of(2023, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(userLogin);
        assertEquals(2, violations.size(), "Создаётся пустой логин");
        violations = validator.validate(userLogin1);
        assertEquals(1, violations.size(), "Создаётся null логин");
    }

    @Test
    public void validateEmailIsBlank() {
        User userEmail = new User("", "login", "name", LocalDate.of(2023, 1, 12));
        User userEmail1 = new User(null, "login", "name", LocalDate.of(2023, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(userEmail);
        assertEquals(1, violations.size(), "Создаётся пустой email");
        violations = validator.validate(userEmail1);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    public void validateNotEmail() {
        User userEmail = new User("@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        User userEmail1 = new User("Test@", "login", "name", LocalDate.of(2023, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(userEmail);
        assertEquals(1, violations.size(), "Создаётся не email");
        violations = validator.validate(userEmail1);
        assertEquals(1, violations.size(), "Создаётся не email");
    }

    @Test
    public void validateBirthdayAfterNow() {
        User userEmail = new User("Test@mail.ru", "login", "name", LocalDate.of(2030, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(userEmail);
        assertEquals(1, violations.size(), "Создался пользователь с днём рожения в будущем");
    }

    @Test
    public void validateLogin() {
        User user = new User("Test@mail.ru", "login new", "name", LocalDate.of(2023, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Создался пользователь с днём рожения в будущем");
    }

    @Test
    public void validateName() {
        User user = new User("Test@mail.ru", "login", "", LocalDate.of(2023, 1, 12));
        service.createUser(user);
        user.setId(5);

        assertEquals(service.getUser(5).getName(), service.getUser(5).getLogin(), "Пустое имя не заменяется на логин.");

        User user1 = new User("Test@mail.ru", "login", null, LocalDate.of(2023, 1, 12));
        service.createUser(user1);
        user.setId(6);

        assertEquals(service.getUser(6).getName(), service.getUser(6).getLogin(), "null имя не заменяется на логин.");
    }

    @Test
    public void getAllUsers() {
        assertEquals(service.getUsers().size(), 3, "Не сохраняет нужное количество.");
        assertNotNull(service.getUsers(), "Не сохраняет ничего.");

        User user = new User("Test@mail.ru", "login", "NAME", LocalDate.of(2023, 1, 12));
        user.setId(1);
        service.updateUser(user);

        assertEquals(service.getUsers().size(), 3, "Добавляет обновлённое.");
    }

    @Test
    public void addFriend() {
        service.addFriend(1, 2);

        assertEquals(1, service.getFriends(1).size(), "Не добавляет друзей у 1 пользователя.");
    }

    @Test
    public void removeFriend() {
        User user = new User("Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        service.createUser(user);
        User user1 = new User("Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        service.createUser(user1);

        service.addFriend(1, 2);
        service.removeFriend(1, 2);

        assertEquals(user.getFriends().size(), 0, "Не удаляет друзей у 1 пользователя");
        assertEquals(user1.getFriends().size(), 0, "Не удаляет друзей у 2 пользователя");
    }

    @Test
    public void getAllFriends() {
        service.addFriend(1, 2);
        service.addFriend(1, 3);
        service.addFriend(1, 4);

        assertNotNull(service.getFriends(1), "Список друзей равен null");
        assertEquals(service.getFriends(1).size(), 3, "Не правильно добавляет нужных друзей");
    }

    @Test
    public void getCommonFriends() {
        User user = new User("Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        service.createUser(user);
        service.addFriend(1, 2);
        service.addFriend(3, 2);

        assertNotNull(service.getCommonFriends(1, 3), "Список общих друзей null");
        assertEquals(service.getCommonFriends(1, 3).size(), 1, "Не правильно рассчитываются общие друзья.");
    }
}
