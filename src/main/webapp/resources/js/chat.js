var lastUpdate;

function run(){
    var appContainer = document.getElementById('chat-area');

    appContainer.addEventListener('click', delegateEvent);

    lastUpdate = getTimestamp(new Date(0));

    if (checkAuthentication()) {
        refreshMessageArea();
    }
    // scrollToTheBottom();

    setInterval(function(){
        if (checkAuthentication()) {
            refreshMessageArea();
        }
    }, 2000);
}

function getTimestamp(date) {
    var result = date.getFullYear() + "-" + st(date.getMonth() + 1) + "-" + st(date.getDate() + 1)
        + " " + st(date.getHours()) + ":" + st(date.getMinutes()) + ":" + st(date.getSeconds())
        + "." + date.getMilliseconds();
    return result;
}

function st(str) {
    str = str.toString();
    while (str.length < 2) {
        str = "0" + str;
    }
    return str;
}

function delegateEvent(evtObj) {
    if(evtObj.type === 'click' && evtObj.target.id == 'add-button'){
        onAddButton(evtObj);
    }
    if(evtObj.type === 'click' && evtObj.target.id === 'enter-button'){
        onEnterButton(evtObj);
    }
    if(evtObj.type === 'click' && evtObj.target.id === 'name-edit-button'){
        onNameEditButton(evtObj);
    }
    if(evtObj.type === 'click' && evtObj.target.id.substring(0, 14) == 'remove-button-'){
        onRemoveButton(evtObj.target.id.substring(14), evtObj);
    }
    if(evtObj.type === 'click' && evtObj.target.id.substring(0, 12) == 'edit-button-'){
        onEditButton(evtObj.target.id.substring(12), evtObj);
    }
}

function checkAuthentication() {
    if (sessionStorage.getItem("user_id") != null) {
        // user
        var user_id = sessionStorage.getItem("user_id");
        var user_name = sessionStorage.getItem("user_name");

        var user_info_box = document.getElementById("user-info-box");
        user_info_box.innerHTML = "Добро пожаловать, " + user_name;

        var buttonItem = document.createElement('button');
        buttonItem.setAttribute('type', 'button');
        buttonItem.setAttribute('id', "name-edit-button");
        buttonItem.classList.add("btn");
        buttonItem.classList.add("btn-xs");
        buttonItem.classList.add("btn-default");
        buttonItem.classList.add("pull-right");
        buttonItem.classList.add("message-button");
        buttonItem.innerHTML = "Edit";

        user_info_box.appendChild(buttonItem);

        document.getElementById("username").disabled = true;
        document.getElementById("password").disabled = true;
        document.getElementById("enter-button").disabled = false;
        document.getElementById("enter-button").innerHTML = "Выйти";
        document.getElementById("message-text").disabled = false;
        document.getElementById("add-button").disabled = false;

        return true;
    } else {
        // guest
        var user_info_box = document.getElementById("user-info-box");
        user_info_box.innerHTML = "Для пользования чатом необходимо пройти аутентификацию";
        document.getElementById("username").disabled = false;
        document.getElementById("password").disabled = false;
        document.getElementById("enter-button").disabled = false;
        document.getElementById("enter-button").innerHTML = "Войти";
        document.getElementById("message-text").disabled = true;
        document.getElementById("add-button").disabled = true;

        var message_area = document.getElementById('message-area');

        while (message_area.firstChild) {
            message_area.removeChild(message_area.firstChild);
        }

        lastUpdate = getTimestamp(new Date(0));

        updateStatusButton(2);

        return false;
    }
}

function onEnterButton(){

    if (!checkAuthentication()) {
        var name = document.getElementById('username').value;
        var password = document.getElementById('password').value;
        var passhash = CryptoJS.MD5(document.getElementById('password').value);

        if (name.length > 0 && password.length > 0) {
            var posting = $.post("/Chat/users", {"type": "authentication",
                "name": name,
                "password": passhash.toString()});
            posting.done(function (data) {
                var user_id = data.user_id;
                if (user_id == -1) {
                    alert("Вы ввели некорретные данные!");
                } else {
                    alert("Вы ввели корретные данные! Добро пожаловать в чат!");
                    document.getElementById('username').value = "";
                    document.getElementById('password').value = "";
                    sessionStorage.setItem("user_id", user_id);
                    sessionStorage.setItem("user_name", name);

                }
                checkAuthentication();
            });
            posting.fail(function (data) {
                alert("Проблемы с соединением!");
                checkAuthentication();
            });
        } else {
            alert("Логин и пароль не могут быть пустыми строками!");
            checkAuthentication();
        }
    } else {
        var response = confirm("Вы действительно хотите выйти из своей учетной записи?");
        if (response) {
            sessionStorage.removeItem("user_id");
            checkAuthentication();
        }
    }
}

function onRemoveButton(message_id) {
    var response = confirm("Вы действительно хотите удалить свое сообщение?");
    if (response) {
        var posting = $.post("/Chat/messages", {"type": "remove_message", "message_id": message_id});
        posting.done( function( data ) {
            refreshMessageArea();
            // scrollToTheBottom();
            updateStatusButton(0);
        });
        posting.fail( function( data ) {
            updateStatusButton(1);
        });
    }
}

function onEditButton(message_id) {
    var messageBox = document.getElementById("message-" + message_id);
    var contentItem = messageBox.getElementsByClassName("content")[0]
        .getElementsByTagName("i")[0];
    var newText = prompt("Введите текст сообщения", contentItem.innerHTML);
    if (newText.length > 0) {
        var response = confirm("Вы действительно хотите изменить свое сообщение?");
        if (response) {
            var posting = $.post("/Chat/messages", {"type": "update_message", "message_id": message_id,
                "message": newText});
            posting.done( function( data ) {
                refreshMessageArea();
                // scrollToTheBottom();
                updateStatusButton(0);
            });
            posting.fail( function( data ) {
                updateStatusButton(1);
            });
        }
    } else {
        alert("Вы не можете оставить сообщение пустым!");
    }
}

function onNameEditButton() {
    var newName = prompt("Введите текст сообщения", sessionStorage.getItem("user_name"));
    if (newName.length > 0) {
        var response = confirm("Вы действительно хотите изменить имя своего пользователя?");
        if (response) {
            var posting = $.post("/Chat/users", {"type": "change_name",
                "id": sessionStorage.getItem("user_id"),
                "name": newName});
            posting.done( function( data ) {
                if (data.verdict == "ok") {
                    sessionStorage.setItem("user_name", newName);
                } else {
                    alert("Пользователь с таким именем уже существует! Выберите другое.");
                }
                refreshMessageArea();
                // scrollToTheBottom();
                updateStatusButton(0);
            });
            posting.fail( function( data ) {
                updateStatusButton(1);
            });
        }
    } else {
        alert("Вы не можете оставить имя пользователя пустым!");
    }
}

function refreshMessageArea(){
    var posting = $.ajax({
        type: "get",
        url: "/Chat/messages",
        data: {"type": "since", "timestamp": lastUpdate}
    });

    posting.done( function( data ) {
        for (i = 0; i < data.length; i++) {
            var message = data[i].message;
            var author = message.user_name;
            var content = message.content;
            var date = message.created_at;
            if (lastUpdate.localeCompare(data[i].created_at) < 0) {
                lastUpdate = data[i].created_at;
            }
            if (data[i].event_type == "add_message") {
                var user_id_session = sessionStorage.getItem("user_id");
                var user_id_message = message.user_id;
                addMessage(message.id, author.bold() + "<" + processDate(date) + ">: "
                + content.italics(), user_id_message == user_id_session);
            }
            if (data[i].event_type == "update_message") {
                updateMessage(message.id, author.bold() + "<" + processDate(date) + ">: "
                + content.italics());
            }
            if (data[i].event_type == "remove_message") {
                removeMessage(message.id);
            }
        }
        updateStatusButton(0);
    });

    posting.fail( function( data ) {
            updateStatusButton(1);
        }
    );

    return true;
}

function processDate(dateStr) {
    var date = new Date(moment(dateStr));
    return st(date.getHours()) + ":" + st(date.getMinutes()) + ":" + st(date.getSeconds())
        + " " + st(date.getDate() + 1) + "-" + st(date.getMonth() + 1) + "-" + date.getFullYear();
}

function updateStatusButton(connectionStatus) {
    var status_button = document.getElementById("status-button");
    if (status_button.classList.contains("btn-warning")) {
        status_button.classList.remove("btn-warning");
    }
    if (status_button.classList.contains("btn-success")) {
        status_button.classList.remove("btn-success");
    }
    if (status_button.classList.contains("btn-danger")) {
        status_button.classList.remove("btn-danger");
    }

    if (connectionStatus == 0) {
        status_button.classList.add("btn-success");
        status_button.innerHTML = "Сервер доступен";
    } else if (connectionStatus == 1) {
        status_button.classList.add("btn-danger");
        status_button.innerHTML = "Сервер не доступен";
    } else {
        status_button.classList.add("btn-warning");
        status_button.innerHTML = "Войдите в чат";
    }
}

function scrollToTheBottom() {
    var objDiv = document.getElementById("message-area");
    objDiv.scrollTop = objDiv.scrollHeight;
}

function onAddButton(){
    var text = document.getElementById('message-text').value;
    if (text.length > 0) {
        var user_id = sessionStorage.getItem("user_id");
        var posting = $.post("/Chat/messages", {"type": "new_message", "user_id": user_id,
            "message": text});
        posting.done( function( data ) {
            refreshMessageArea();
            // scrollToTheBottom();
            updateStatusButton(0);
        });
        posting.fail( function( data ) {
            updateStatusButton(1);
        });
    } else {
        alert("Вы не можете отправить пустое сообщение!");
    }
    document.getElementById('message-text').value = "";
}

function addMessage(message_id, content, is_author) {
    var message = createMessage(message_id, content, is_author);
    var message_area = document.getElementById('message-area');

    message_area.appendChild(message);
}

function updateMessage(message_id, content) {
    var message = document.getElementById("message-" + message_id.toString());

    message.getElementsByTagName("u")[0].innerHTML = content;
}

function removeMessage(message_id) {
    var message = document.getElementById("message-" + message_id.toString());
    while (message.firstChild) {
        message.removeChild(message.firstChild);
    }
    message.parentNode.removeChild(message);
}

function createButton(message_id, name) {
    var buttonItem = document.createElement('button');
    buttonItem.setAttribute('type', 'button');
    buttonItem.setAttribute('id', name + "-button-" + message_id.toString());
    buttonItem.classList.add("btn");
    buttonItem.classList.add("btn-xs");
    buttonItem.classList.add("btn-default");
    buttonItem.classList.add("pull-right");
    buttonItem.classList.add("message-button");
    buttonItem.innerHTML = name;
    return buttonItem;
}

function createMessageBox(content) {
    var divItem = document.createElement('div');
    //divItem.classList.add("col-md-8");
    divItem.classList.add('message');
    divItem.classList.add("message-content");
    divItem.innerHTML = content;
    return divItem;
}

function createMessage(message_id, content, is_author){
    var divItem = document.createElement('div');
    divItem.setAttribute("id", "message-" + message_id.toString());
    divItem.classList.add('message');
    divItem.classList.add("message-content");

    var contentItem = document.createElement('u');
    contentItem.innerHTML = content;

    divItem.appendChild(contentItem);

    if (is_author) {
        var buttonEdit = createButton(message_id, 'edit');
        var buttonRemove = createButton(message_id, 'remove');

        divItem.appendChild(buttonRemove);
        divItem.appendChild(buttonEdit);
    }

    return divItem;
}

