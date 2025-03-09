# java-filmorate

Template repository for Filmorate project.  
Ссылка на схему: <https://app.quickdatabasediagrams.com/#/d/B47gOs>  

Получение фильмов:  
SELECT *  
FROM Film;        
Получение жанра фильма с названием "Интерстеллар"  
SELECT g.name  
FROM Film AS f  
INNER JOIN Genre AS g ON g.IdGenre=f.IdGenre  
WHERE f.name='Интерстеллар';  

Получение логина конкретного пользователя:  
SELECT login  
FROM User  
WHERE name='NAME';  
Получение друзей конкретного пользователя:  
SELECT IdFriend  
FROM User AS u  
INNER JOIN Friends AS F ON f.IdUser=u.IdUser  
WHERE u.name='NAME';  