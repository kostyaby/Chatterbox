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
    alert(evtObj.target.id);
    if(evtObj.type === 'click' && evtObj.target.id.substring(0, 13) === 'remove-button'){
        onRemoveButton(evtObj.target.id.substring(14), evtObj);
    }
}

function checkAuthentication() {
    if (sessionStorage.getItem("user_id") != null) {
        // user
        var user_id = sessionStorage.getItem("user_id");

        var user_info_box = document.getElementById("user-info-box");
        user_info_box.innerHTML = "Вы успешно прошли аутентификацию. Поздравляю!";
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
            var posting = $.post("/Chat/users", {"name": name, "password": passhash.toString()});
            posting.done(function (data) {
                var user_id = data.user_id;
                if (user_id == -1) {
                    alert("Вы ввели некорретные данные!");
                } else {
                    alert("Вы ввели корретные данные! Добро пожаловать в чат!");
                    document.getElementById('username').value = "";
                    document.getElementById('password').value = "";
                    sessionStorage.setItem("user_id", user_id);
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
    alert(message_id);
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
                addMessage(message.id, author.bold() + "<" + processDate(date) + ">: "
                    + content);
            }
            if (data[i].event_type == "update_message") {
                updateMessage(message.id, author.bold() + "<" + processDate(date) + ">: "
                    + content);
            }
            if (data[i].event_type == "delete_message") {
                deleteMessage(message.id);
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

function addMessage(message_id, content) {
    var message = createMessage(message_id, content);
    var message_area = document.getElementById('message-area');

    message_area.appendChild(message);
}

function updateMessage(message_id, content) {
    var message = document.getElementById("message-" + message_id.toString());

    message.innerHTML = content;
}

function deleteMessage(message_id) {
    var message = document.getElementById("message-" + message_id.toString());
    message.parentNode.removeChild(message);
}

function createIconSpan(glyphicon_name) {
    var spanItem = document.createElement('span');
    spanItem.classList.add('glyphicon');
    spanItem.classList.add('glyphicon-' + glyphicon_name)
    spanItem.setAttribute('aria-hidden', 'true');
    return spanItem;
}

function createIconButton(message_id, glyphicon_name) {
    var buttonItem = document.createElement('button');
    buttonItem.setAttribute('type', 'button');
    buttonItem.setAttribute('id', glyphicon_name + "-button-" + message_id.toString());
    alert(glyphicon_name + "-button-" + message_id.toString());
    buttonItem.classList.add("btn");
    buttonItem.classList.add("btn-xs");
    buttonItem.classList.add("btn-default");
    // buttonItem.classList.add("pull-right");
    buttonItem.classList.add("icon-padding");
    buttonItem.appendChild(createIconSpan(glyphicon_name));
    return buttonItem;
}

function createMessage(message_id, content){
    var divItem = document.createElement('div');
    divItem.classList.add('message');
    divItem.setAttribute("id", "message-" + message_id.toString());
    var buttonItem = createIconButton(message_id, 'remove');
    divItem.appendChild(buttonItem);
    return divItem;
}
