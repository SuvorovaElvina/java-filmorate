package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*public class UserControllerTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void createNewUser() {
        User user = new User(1, "Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        UserController controller = new UserController();
        User newUser = controller.create(user);

        assertNotNull(newUser, "Не создаёт пользователь - null");
        assertEquals(newUser.toString(), user.toString(), "Создаёт неправильного пользователя");
    }

    @Test
    public void updateUser() {
        User user = new User(1, "Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        UserController controller = new UserController();
        controller.create(user);
        User user1 = new User(1, "Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        User newUser = controller.update(user1);

        assertNotNull(newUser, "Не обновляет пользователя - null");
        assertEquals(1, newUser.getId(), "Обнавляет не того пользователя");
        assertEquals(newUser.toString(), user1.toString(), "Не обновляет пользователя");
    }

    @Test
    public void validateLoginIsBlank() {
        User userLogin = new User(1, "Test@mail.ru", "", "name", LocalDate.of(2023, 1, 12));
        User userLogin1 = new User(1, "Test@mail.ru", null, "name", LocalDate.of(2023, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(userLogin);
        assertEquals(2, violations.size(), "Создаётся пустой логин");
        violations = validator.validate(userLogin1);
        assertEquals(1, violations.size(), "Создаётся null логин");
    }

    @Test
    public void validateEmailIsBlank() {
        User userEmail = new User(1, "", "login", "name", LocalDate.of(2023, 1, 12));
        User userEmail1 = new User(1, null, "login", "name", LocalDate.of(2023, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(userEmail);
        assertEquals(1, violations.size(), "Создаётся пустой email");
        violations = validator.validate(userEmail1);
        assertEquals(1, violations.size(), "Создаётся null email");
    }

    @Test
    public void validateNotEmail() {
        User userEmail = new User(1, "@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        User userEmail1 = new User(1, "Test@", "login", "name", LocalDate.of(2023, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(userEmail);
        assertEquals(1, violations.size(), "Создаётся не email");
        violations = validator.validate(userEmail1);
        assertEquals(1, violations.size(), "Создаётся не email");
    }

    @Test
    public void validateBirthdayAfterNow() {
        User userEmail = new User(1, "Test@mail.ru", "login", "name", LocalDate.of(2030, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(userEmail);
        assertEquals(1, violations.size(), "Создался пользователь с днём рожения в будущем");
    }

    @Test
    public void validateLogin() {
        User user = new User(1, "Test@mail.ru", "login new", "name", LocalDate.of(2023, 1, 12));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Создался пользователь с днём рожения в будущем");
    }

    @Test
    public void validateName() {
        User user = new User(1, "Test@mail.ru", "login", "", LocalDate.of(2023, 1, 12));
        UserController controller = new UserController();
        User newUser = controller.create(user);

        assertEquals(newUser.getName(), user.getLogin(), "Пустое имя не заменяется на логин.");

        User user1 = new User(2, "Test@mail.ru", "login", null, LocalDate.of(2023, 1, 12));
        User newUser1 = controller.create(user1);

        assertEquals(newUser1.getName(), user1.getLogin(), "null имя не заменяется на логин.");
    }

    @Test
    public void getAllUsers() {
        User user = new User(1, "Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        UserController controller = new UserController();
        controller.create(user);
        User user1 = new User(2, "Test@mail.ru", "login", "name", LocalDate.of(2023, 1, 12));
        controller.create(user1);

        assertEquals(controller.users().size(), 2, "Не сохраняет нужное количество.");
        assertNotNull(controller.users(), "Не сохраняет ничего.");

        User user2 = new User(2, "Test@mail.ru", "login", "NAME", LocalDate.of(2023, 1, 12));
        controller.update(user2);

        assertEquals(controller.users().size(), 2, "Добавляет обновлённое.");
    }
}*/
