# lab

## requests
I am using just a simple curl

```
curl -X POST http://localhost:8080/user -H "Content-Type: application/json;charset=UTF-8" -d '{"id": 1, "name": "Felipe", "email": "felipe@email.com"}'
```

## rabbitmq
I am using docker

```
docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```