var socket = new WebSocket("ws://localhost:8080/socket");
socket.onmessage = function (event) {
    var data = event.data;
    alert(data);
};

var stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    stompClient.subscribe("/topic/newReview", function (message) {
        var review = JSON.parse(message.body);
    });
});


function login() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    fetch("/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
    })
        .then((response) => {
            if (response.status === 401) {
                throw new Error("Invalid credentials");
            }
            const token = response.headers.get("Authorization");
            localStorage.setItem("token", token);
            return response.json();
        })
        .then((user) => {
            localStorage.setItem("userId", user.id);
            window.location.href = "/home";
        })
        .catch((error) => {
            console.error(error);
        });
}

function getHeaders() {
    const token = localStorage.getItem("token");
    return { Authorization: token };
}

function subscribe(boardId) {
    const userId = localStorage.getItem("userId");
    fetch(`/subscribe?boardId=${boardId}&userId=${userId}`, {
        method: "POST",
        headers: { ...getHeaders(), "Content-Type": "application/json" },
    }).catch((error) => {
        console.error(error);
    });
}
