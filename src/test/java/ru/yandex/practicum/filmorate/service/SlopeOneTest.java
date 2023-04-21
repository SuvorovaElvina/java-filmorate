package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlopeOneTest {


    @Test
    public void slopeOneTest() {
        Film film1 = Film.builder()
                .id(1)
                .name("Name1")
                .description("description")
                .releaseDate(LocalDate.of(2002, 12, 12))
                .duration((long) 200)
                .build();

        User user1 = User.builder()
                .id(1)
                .name("name1")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2002, 12, 12))
                .login("login1")
                .build();

        Film film2 = Film.builder()
                .id(2)
                .name("Name2")
                .description("description")
                .releaseDate(LocalDate.of(2002, 12, 12))
                .duration((long) 200)
                .build();

        User user2 = User.builder()
                .id(2)
                .name("name2")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2002, 12, 12))
                .login("login2")
                .build();

        Film film3 = Film.builder()
                .id(3)
                .name("Name3")
                .description("description")
                .releaseDate(LocalDate.of(2002, 12, 12))
                .duration((long) 200)
                .build();

        User user3 = User.builder()
                .id(3)
                .name("name3")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2002, 12, 12))
                .login("login3")
                .build();
        Map<User, HashMap<Film, Double>> inputData = new HashMap<>();
        //user1
        HashMap<Film, Double> data11 = new HashMap<>();
        data11.put(film1, Double.valueOf(5.0));
        data11.put(film2, Double.valueOf(3.0));
        data11.put(film3, Double.valueOf(2.0));

        inputData.put(user1, data11);
        //user2
        HashMap<Film, Double> data23 = new HashMap<>();

        data23.put(film1, Double.valueOf(3.0));
        data23.put(film2, Double.valueOf(4.0));

        inputData.put(user2, data23);

        //user3

        HashMap<Film, Double> data32 = new HashMap<>();
        data32.put(film2, Double.valueOf(2.0));
        data32.put(film3, Double.valueOf(5.0));

        inputData.put(user3, data32);
        List<Film> films = new ArrayList<>();
        films.add(film1);
        films.add(film2);
        films.add(film3);
        inputData = (SlopeOne.slopeOne(inputData, films));
        assertEquals(inputData.get(user3).get(film1), 4.375);
    }


}