package com.chatterbox.models;

import org.javalite.activejdbc.Model;
import org.json.JSONObject;

public class Message extends Model {

    static {
        validatePresenceOf("user_id", "content");
    }

    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        result.put("id", get("id"));
        result.put("user_id", get("user_id"));
        result.put("user_name", User.findFirst("id = ?", get("user_id")).get("name"));
        result.put("content", get("content"));
        result.put("created_at", get("created_at"));
        result.put("updated_at", get("updated_at"));
        return result;
    }

}

