package com.chatterbox.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chatterbox.models.Message;
import com.chatterbox.models.MessageEvent;
import com.chatterbox.models.Model;
import com.chatterbox.models.User;
import com.chatterbox.utils.Base;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        Base base = new Base();
        base.open(ServletConstants.DB_DRIVER,
                ServletConstants.DB_NAME,
                ServletConstants.DB_USERNAME,
                ServletConstants.DB_PASSWORD);
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String parameter = request.getParameter("type");
            if ("all".equals(parameter)) {
                JSONArray array = new JSONArray();

                List<Model> messages = new Message(base).findAll();
                for (Model model : messages) {
                    Message message = new Message(base, model.getId() );
                    array.put(message.toJson());
                }
                out.print(array);
            }
            if ("since".equals(parameter)) {
                String timestamp = request.getParameter("timestamp");
                JSONArray array = new JSONArray();
                List<Model> messageEvents = new MessageEvent(base).where(
                        "created_at > ?::timestamp", timestamp);
                for (Model model : messageEvents) {
                    MessageEvent messageEvent = new MessageEvent(base, model.getId() );
                    array.put(messageEvent.toJson());
                }
                out.print(array);
            }
            out.flush();
            out.close();
        } catch(IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            base.close();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        Base base = new Base();
        base.open(ServletConstants.DB_DRIVER,
            ServletConstants.DB_NAME,
            ServletConstants.DB_USERNAME,
            ServletConstants.DB_PASSWORD);
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String parameter = request.getParameter("type");
            if ("new_message".equals(parameter)) {

                int userId = Integer.parseInt(request.getParameter("user_id"));
                String text = request.getParameter("message");

                Message message = new Message(base);
                message.set("user_id", userId);
                message.set("content", text);
                message.saveIt();

                MessageEvent messageEvent = new MessageEvent(base);
                messageEvent.set("message_id", message.get("id"));
                messageEvent.set("event_type", "add_message");
                messageEvent.saveIt();

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("verdict", "ok");
                out.print(jsonResponse);

                // ~--~

                StringTokenizer tokenizer = new StringTokenizer(new User(base, userId).get("name").toString() );
                Logger logger = Logger.getLogger(getClass().getName() );
                logger.log(Level.INFO, "New message -> "
                        + message.get("created_at") + " "
                        + tokenizer.nextToken() + " : "
                        + message.get("content") );
            }
            if ("update_message".equals(parameter)) {

                int messageId = Integer.parseInt(request.getParameter("message_id"));
                String text = request.getParameter("message");

                Model model = new Message(base).findFirst("id = ?", messageId);
                Message message = new Message(base, model.getId() );
                message.set("content", text);
                message.saveIt();

                MessageEvent messageEvent = new MessageEvent(base);
                messageEvent.set("message_id", messageId);
                messageEvent.set("event_type", "update_message");
                messageEvent.saveIt();

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("verdict", "ok");
                out.print(jsonResponse);

                // ~--~

                StringTokenizer tokenizer = new StringTokenizer(
                        new User(base, Integer.parseInt(message.get("user_id").toString() ) ).get("name").toString() );
                Logger logger = Logger.getLogger(getClass().getName() );
                logger.log(Level.INFO, "Update message -> "
                        + message.get("created_at") + " "
                        + tokenizer.nextToken() + " : "
                        + message.get("content") );
            }
            if ("remove_message".equals(parameter)) {

                int messageId = Integer.parseInt(request.getParameter("message_id"));

                MessageEvent messageEvent = new MessageEvent(base);
                messageEvent.set("message_id", messageId);
                messageEvent.set("event_type", "remove_message");
                messageEvent.saveIt();

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("verdict", "ok");
                out.print(jsonResponse);

                // ~--~
                Message message = new Message(base, messageId);

                StringTokenizer tokenizer = new StringTokenizer(
                        new User(base, Integer.parseInt(message.get("user_id").toString() ) ).get("name").toString() );
                Logger logger = Logger.getLogger(getClass().getName() );
                logger.log(Level.INFO, "Delete message -> "
                        + message.get("created_at") + " "
                        + tokenizer.nextToken() + " : "
                        + message.get("content") );
            }
            out.flush();
            out.close();
        } catch(IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            base.close();
        }
    }
}
