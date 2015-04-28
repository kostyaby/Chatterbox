package com.chatterbox.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chatterbox.models.Message;
import com.chatterbox.models.MessageEvent;
import com.chatterbox.models.Model;
import com.chatterbox.models.User;
import com.chatterbox.utils.Base;
import org.json.JSONException;
import org.json.JSONObject;

public class UserServlet extends HttpServlet {
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

            String name = request.getParameter("name");
            String password = request.getParameter("password");
            List<Model> users = new User(base).where("name=?", name);
            if (users.size() != 1) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("user_id", -1);
                out.print(jsonResponse);
            } else {
                Model model = users.get(0);
                User user = new User(base, model.getId() );
                JSONObject jsonResponse = new JSONObject();
                if (user.get("password").equals(password)) {
                    jsonResponse.put("user_id", user.get("id"));
                } else {
                    jsonResponse.put("user_id", -1);
                }
                out.print(jsonResponse);
            }
            out.flush();
            out.close();

        } catch(IOException| JSONException e) {
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

            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            if (new User(base).where("name = ?", name).size() > 0) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("verdict", "wa");
                out.print(jsonResponse);
            } else {
                Model _model = new User(base).findFirst("id = ?", id);
                User user = new User(base, _model.getId() );
                user.set("name", name);
                user.saveIt();
                List<Model> models = new Message(base).where("user_id = ?", id);
                for (Model model : models) {
                    Message message = new Message(base, model.getId() );

                    MessageEvent messageEvent = new MessageEvent(base);
                    messageEvent.set("message_id", message.get("id"));
                    messageEvent.set("event_type", "update_message");

                    messageEvent.saveIt();
                }

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("verdict", "ok");
                out.print(jsonResponse);
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
