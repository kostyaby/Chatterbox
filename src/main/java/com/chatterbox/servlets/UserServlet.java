package com.chatterbox.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chatterbox.models.Message;
import com.chatterbox.models.User;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.json.JSONArray;
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
        Base.open(DB_DRIVER, DB_NAME, DB_USERNAME, DB_PASSWORD);
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                out.write(paramName);
                out.write("n");
                String[] paramValues = request.getParameterValues(paramName);
                for (String paramValue : paramValues) {
                    out.write("t" + paramValue);
                    out.write("n");
                }

            }
            out.flush();
            out.close();
        } catch(IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void destroy() {
        Base.close();
        super.destroy();
    }
}
