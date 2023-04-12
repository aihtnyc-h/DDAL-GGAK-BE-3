{
    "users": [
    {
        "id": 1,
        "name": "Alice",
        "email": "alice@example.com"
    },
    {
        "id": 2,
        "name": "Bob",
        "email": "bob@example.com"
    },
    {
        "id": 3,
        "name": "Charlie",
        "email": "charlie@example.com"
    }
],
    "rooms": [
    {
        "id": 1,
        "name": "General",
        "description": "General chat room",
        "members": [1, 2, 3],
        "messages": [
            {
                "id": 1,
                "sender": 1,
                "content": "Hello, everyone!",
                "timestamp": "2023-04-06T10:00:00"
            },
            {
                "id": 2,
                "sender": 2,
                "content": "Hi, Alice!",
                "timestamp": "2023-04-06T10:01:00"
            }
        ]
    },
    {
        "id": 2,
        "name": "Random",
        "description": "Random chat room",
        "members": [1, 3],
        "messages": []
    }
]
}
