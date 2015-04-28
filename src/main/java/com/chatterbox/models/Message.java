package com.chatterbox.models;

import com.chatterbox.utils.Base;
import org.json.JSONObject;

public class Message extends Model {

    public Message(Base base) {
        super(base);
        name = "messages";
    }

    public Message(Base base, int id) {
        super(base, id);
        name = "messages";
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", get("id") );
        jsonObject.put("user_id", get("user_id") );
        User user = new User(base, (int)get("user_id") );
        jsonObject.put("user_name", user.get("name") );
        jsonObject.put("content", get("content") );
        jsonObject.put("created_at", get("created_at") );
        jsonObject.put("updated_at", get("updated_at") );
        return jsonObject;
    }

}

