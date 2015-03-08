var lastUpdate;
var countOfMessages = 0;
var connectionStatus = false;

function run(){
    var appContainer = document.getElementById('chat-area');

    appContainer.addEventListener('click', delegateEvent);

    lastUpdate = getTimestamp(new Date(0));
    scrollHeight = document.getElementById("message-area").scrollHeight;

    refreshMessageArea();
    scrollToTheBottom();

    setInterval(function(){
        refreshMessageArea();
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
}

function refreshMessageArea(){
    var posting = $.get( "/Chat/messages", { "type": "since", "timestamp": lastUpdate } );

    posting.done( function( data ) {
        for (i = 0; i < data.length; i++) {
            var content = data[i].content;
            if (lastUpdate.localeCompare(data[i].created_at) < 0) {
                lastUpdate = data[i].created_at;
            }
            countOfMessages += 1;
        }
        for (i = 0; i < data.length; i++) {
            var content = data[i].content;
            addMessage(content);
        }
        connectionStatus = true;
        updateStatusButton();
    });

    posting.fail( function( data ) {
            connectionStatus = false;
            updateStatusButton();
        }
    );

    return true;
}

function updateStatusButton() {
    var status_button = document.getElementById("status-button");
    if (status_button.classList.contains("btn-success")) {
        status_button.classList.remove("btn-success");
    }
    if (status_button.classList.contains("btn-danger")) {
        status_button.classList.remove("btn-danger");
    }

    if (connectionStatus == true) {
        status_button.classList.add("btn-success");
        status_button.innerHTML = "Сервер доступен";
    } else {
        status_button.classList.add("btn-danger");
        status_button.innerHTML = "Сервер не доступен";
    }
}

function scrollToTheBottom() {
    var iterations = 0;
    while (true) {
        //console.log(countOfMessages);
        //console.log(document.getElementsByClassName("message").length);
        if (countOfMessages == document.getElementsByClassName("message").length) {
            var objDiv = document.getElementById("message-area");
            objDiv.scrollTop = objDiv.scrollHeight;
            //alert(iterations);
            break;
        } else {
            iterations += 1;
        }
    }
}

function onAddButton(){
    var text = document.getElementById('message-text').value;
    if (text.length > 0) {
        var posting = $.post("/Chat/messages", {"type": "new_message", "message": text});
        posting.done( function( data ) {
            refreshMessageArea();
            scrollToTheBottom();
            connectionStatus = true;
            updateStatusButton();
        });
        posting.fail( function( data ) {
            connectionStatus = false;
            updateStatusButton();
        });
    }
    document.getElementById('message-text').value = "";
}

function addMessage(content) {
    var message = createMessage(content);
    var message_area = document.getElementById('message-area');

    message_area.appendChild(message);
}

function createMessage(content){
    var divItem = document.createElement('div');
    divItem.classList.add('message');
    divItem.appendChild(document.createTextNode(content));
    return divItem;
}
