package com.chatterbox.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chatterbox.models.Message;
import com.chatterbox.models.MessageEvent;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageServlet extends HttpServlet {

    final private String DB_DRIVER = "org.postgresql.Driver";
    final private String DB_NAME = "jdbc:postgresql://localhost:5432/chatterbox";
    final private String DB_USERNAME = "kostya_by";
    final private String DB_PASSWORD = "";
    @Override
    public void init() throws ServletException {
        super.init();
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        Base.open(DB_DRIVER, DB_NAME, DB_USERNAME, DB_PASSWORD);
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String parameter = request.getParameter("type");
            if ("all".equals(parameter)) {
                JSONArray array = new JSONArray();
                List<Message> messages = Message.findAll().orderBy("id");
                for (Model model : messages) {
                    Message message = (Message) model;
                    array.put(message.toJson());
                }
                out.print(array);
            }
            if ("since".equals(parameter)) {
                String timestamp = request.getParameter("timestamp");
                JSONArray array = new JSONArray();
                List<MessageEvent> messageEvents = MessageEvent.where(
                    "created_at > ?::timestamp", timestamp).orderBy("id");
                for (Model model : messageEvents) {
                    MessageEvent messageEvent = (MessageEvent) model;
                    array.put(messageEvent.toJson());
                }
                out.print(array);
            }
            out.flush();
            out.close();
        } catch(IOException | JSONException e) {
            e.printStackTrace();
        }
        Base.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        Base.open(DB_DRIVER, DB_NAME, DB_USERNAME, DB_PASSWORD);
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String parameter = request.getParameter("type");
            if ("new_message".equals(parameter)) {

                int userId = Integer.parseInt(request.getParameter("user_id"));
                String text = request.getParameter("message");

                Message message = new Message();
                message.set("user_id", userId);
                message.set("content", text);
                message.saveIt();

                MessageEvent messageEvent = new MessageEvent();
                messageEvent.set("message_id", message.get("id"));
                messageEvent.set("event_type", "add_message");
                messageEvent.saveIt();

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("verdict", "ok");
                out.print(jsonResponse);
            }
            if ("update_message".equals(parameter)) {

                int messageId = Integer.parseInt(request.getParameter("message_id"));
                String text = request.getParameter("message");

                Message message = Message.findFirst("id = ?", messageId);
                message.set("content", text);
                message.saveIt();

                MessageEvent messageEvent = new MessageEvent();
                messageEvent.set("message_id", messageId);
                messageEvent.set("event_type", "update_message");
                messageEvent.saveIt();

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("verdict", "ok");
                out.print(jsonResponse);
            }
            if ("remove_message".equals(parameter)) {

                int messageId = Integer.parseInt(request.getParameter("message_id"));

                MessageEvent messageEvent = new MessageEvent();
                messageEvent.set("message_id", messageId);
                messageEvent.set("event_type", "remove_message");
                messageEvent.saveIt();

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("verdict", "ok");
                out.print(jsonResponse);
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
