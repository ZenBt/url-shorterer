# Билд
```
mvn clean install -U    
```

# Эндпоинты

POST http://localhost:8080/url/shorten?url=https://example.com&userId=cfa673a4-f621-472c-bf85-17caf6c0edd6&maxRedirects=5

Параметры userId, maxRedirects - опциональные
url - обязательный

GET http://localhost:8080/url/{shortedUrl} 

DELETE http://localhost:8080/url/{shortedUrl}/{userId}

PUT http://localhost:8080/url/{shortedUrl}/{userId}?maxRedirects=1
maxRedirects - обязательный