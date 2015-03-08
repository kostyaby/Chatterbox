package com.chatterbox.models;

import org.javalite.activejdbc.Model;

public class User extends Model {

    static {
        validatePresenceOf("name", "password", "email");
        validateEmailOf("email");
    }

}


