# java-filmorate
Template repository for Filmorate project.
### ER Diagram:

<img height="500" src="https://raw.githubusercontent.com/Dantelain/java-filmorate/add-database/ER%20Diagram.svg" title="ER-Diagram" width="1200"/>

### Запросы:
Вывести список всех пользователей:
 ```
 SELECT * FROM users;
 ```
Вывести список всех фильмов:
 ```
 SELECT * FROM films;
 ```
Узнать рейтинг фильмов:
 ```
 SELECT 
   films.*,
   count(fl.film_id) rating
 FROM films
 JOIN film_likes fl ON fl.film_id=films.film_id
 GROUP BY fl.film_id;
 ```
Узнать ТОП-10 фильмов по рейтингу:
 ```
 SELECT 
   films.*,
   count(fl.film_id) rating
 FROM films
 JOIN film_likes fl ON fl.film_id=films.film_id
 GROUP BY fl.film_id
 ORDER BY rating DESC -- сортируем по убыванию (от большего к меньшему)
 LIMIT 10;
 ```

Список общих друзей:
 ```
 SELECT friend_id FROM friends 
 WHERE user_id = 1
 AND friend_id in (
   SELECT friend_id FROM friends 
   WHERE user_id = 2; 
 );


