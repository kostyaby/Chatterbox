package com.chatterbox.models;

import org.javalite.activejdbc.Model;
import org.json.JSONObject;

public class MessageEvent extends Model {

    static {
        validatePresenceOf("message_id", "event_type");
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event_type", get("event_type"));
        jsonObject.put("message", ((Message)Message.findFirst("id = ?", get("message_id")))
            .toJson());
        jsonObject.put("created_at", get("created_at"));
        return jsonObject;
    }

}
