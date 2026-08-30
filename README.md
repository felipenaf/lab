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

To see the status
```
http://localhost:15672/#/queues
```
## Images
PATCH request 
![Screenshot from 2026-08-30 17-54-51.png](screenshot/Screenshot%20from%202026-08-30%2017-54-51.png)

POST request
![Screenshot from 2026-08-30 17-58-14.png](screenshot/Screenshot%20from%202026-08-30%2017-58-14.png)

> I configured a TOPIC exchange and put to Notification accept every order, 
> like "order.*" and Inventory accepts just the whole case "order.created"  

## Miro Diagram
```
https://miro.com/app/board/uXjVHVMxylA=/?focusWidget=3458764682159659437
```

## Brainstorming
I am learning how to implement with chatgpt

```
https://chatgpt.com/c/6a661b0d-eae4-83e9-8c7a-c97ec1b3c0cb
```

## Report

-[ ] I need to understand how to get again an unacked message
-[ ] Create another queues to understand the behaviors