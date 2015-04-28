package com.chatterbox.models;

import com.chatterbox.utils.Base;

public class User extends Model {

    public User(Base base) {
        super(base);
        name = "users";
    }

    public User(Base base, int id) {
        super(base, id);
        name = "users";
    }

}


