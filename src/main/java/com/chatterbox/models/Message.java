package com.chatterbox.models;

import org.javalite.activejdbc.Model;
import org.json.JSONObject;

public class Message extends Model {

    static {
        validatePresenceOf("user_id", "content");
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", get("id"));
        jsonObject.put("user_id", get("user_id"));
        jsonObject.put("user_name", User.findFirst("id = ?", get("user_id")).get("name"));
        jsonObject.put("content", get("content"));
        jsonObject.put("created_at", get("created_at"));
        jsonObject.put("updated_at", get("updated_at"));
        return jsonObject;
    }

}

