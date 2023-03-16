# База данных:

![DataBase](https://user-images.githubusercontent.com/114740144/225637154-a16ff3e2-36fb-4756-a1c4-58a4e3131220.jpg)

# Примеры запросов: 
## Вызов топ 5 популярных фильмов по лайкам.
```
SELECT f.title; COUNT(fl.user_id)
FROM film AS f
JOIN film_like AS fl ON fl.film_id = f.film_id
GROUP BY title
ORDER BY COUNT(fl.user_id) DESC
LIMIT 5;
```
***
## Получение жанра фильмов.
```
SELECT title
FROM film AS f
JOIN genre AS g ON g.genre_id = f.genre_id
GROUP BY title;
```
***
## Получение пользователей которые не ввели имя.
```
SELECT name
FROM user
WHERE login = name;
```
