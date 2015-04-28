package com.chatterbox.models;

import com.chatterbox.utils.Base;
import org.json.JSONObject;

public class MessageEvent extends Model {

    public MessageEvent(Base base) {
        super(base);
        name = "message_events";
    }

    public MessageEvent(Base base, int id) {
        super(base, id);
        name = "message_events";
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event_type", get("event_type") );
        Message message = new Message(base, (int)get("message_id") );
        jsonObject.put("message", message.toJson() );
        jsonObject.put("created_at", get("created_at") );
        return jsonObject;
    }

}
