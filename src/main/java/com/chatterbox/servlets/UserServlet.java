package com.chatterbox.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chatterbox.models.Message;
import com.chatterbox.models.MessageEvent;
import com.chatterbox.models.User;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.json.JSONException;
import org.json.JSONObject;

public class UserServlet extends HttpServlet {

    final private String DB_DRIVER = "org.postgresql.Driver";
    final private String DB_NAME = "jdbc:postgresql://localhost:5432/chatterbox";
    final private String DB_USERNAME = "kostya_by";
    final private String DB_PASSWORD = "";
    @Override
    public void init() throws ServletException {
        super.init();
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        Base.open(DB_DRIVER, DB_NAME, DB_USERNAME, DB_PASSWORD);
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String parameter = request.getParameter("type");

            if ("authentication".equals(parameter)) {
                String name = request.getParameter("name");
                String password = request.getParameter("password");
                List<User> users = User.where("name=?", name);
                if (users.size() != 1) {
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("user_id", -1);
                    out.print(jsonResponse);
                } else {
                    User user = users.get(0);
                    JSONObject jsonResponse = new JSONObject();
                    if (user.get("password").equals(password)) {
                        jsonResponse.put("user_id", user.get("id"));
                    } else {
                        jsonResponse.put("user_id", -1);
                    }
                    out.print(jsonResponse);
                }
            }
            if ("change_name".equals(parameter)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("name");
                if (User.where("name = ?", name).size() > 0) {
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("verdict", "wa");
                    out.print(jsonResponse);
                } else {
                    User user = User.findFirst("id = ?", id);
                    user.set("name", name);
                    user.saveIt();
                    List<Model> models = Message.where("user_id = ?", id);
                    for (Model model : models) {
                        Message message = (Message)model;

                        MessageEvent messageEvent = new MessageEvent();
                        messageEvent.set("message_id", message.get("id"));
                        messageEvent.set("event_type", "update_message");

                        messageEvent.saveIt();
                    }
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("verdict", "ok");
                    out.print(jsonResponse);
                }
            }
            out.flush();
            out.close();
        } catch(IOException | JSONException e) {
            e.printStackTrace();
        }
        Base.close();
    }
    @Override
    public void destroy() {
        super.destroy();
    }
}
