package com.chatterbox.models;

import com.chatterbox.utils.Base;
import com.chatterbox.utils.DBException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Model {
    Base base;
    String name;
    int id = -1;
    Map<String, Object> unsavedChanges = new TreeMap<>();

    Model(Base base) {
        this.base = base;
    }

    Model(Base base, int id) {
        this.base = base;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Model> findAll() {
        try {
            String sql = "SELECT id FROM " + name;
            PreparedStatement statement = base.connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();
            List<Model> result = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                Model model = new Model(base, id);
                result.add(model);
            }
            return result;
        } catch (Exception e) {
            throw new DBException("Something went wrong within FIND_ALL method of Model class!", e);
        }
    }

    public List<Model> where(String pattern, Object... parameters) {
        try {
            String sql = "SELECT id FROM " + name + " WHERE " + pattern;
            PreparedStatement statement = base.connection.prepareStatement(sql);
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }

            ResultSet resultSet = statement.executeQuery();
            List<Model> result = new ArrayList<>();
            while (resultSet.next() ) {
                int id = resultSet.getInt("id");
                Model model = new Model(base, id);
                result.add(model);
            }
            return result;
        } catch (Exception e) {
            throw new DBException("Something went wrong within WHERE method of Model class!", e);
        }
    }

    public Model findFirst(String pattern, Object... parameters) {
        pattern += " LIMIT 1";
        List<Model> models = where(pattern, parameters);
        if (models.size() == 0) {
            return null;
        } else {
            return models.get(0);
        }
    }

    public void set(String key, Object value) {
        try {
            unsavedChanges.put(key, value);
        } catch (Exception e) {
            throw new DBException("Something went wrong within SET method of Model class!", e);
        }
    }

    public Object get(String key) {
        try {
            if (id == -1) {
                throw new DBException("Couldn't request anything for unsaved model!");
            }
            Model model = findFirst("id = ?", id);

            String sql = "SELECT " + key + " FROM " + name + " WHERE id = ?";
            PreparedStatement statement = base.connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            Object result = null;
            while (resultSet.next() ) {
                if (result == null) {
                    result = resultSet.getObject(key);
                } else {
                    throw new DBException("This should has happened!");
                }
            }
            return result;

        } catch (Exception e) {
            throw new DBException("Something went wrong within GET method of Model class!", e);
        }
    }

    public synchronized void saveIt() {
        if (unsavedChanges.size() == 0) {
            // throw new DBException("No changes where made, nothing to save");
            return;
        }

        Timestamp timestamp = getTimeStamp();

        unsavedChanges.put("updated_at", timestamp);

        if (id == -1) {
            unsavedChanges.put("created_at", timestamp);
            insertNewInstance();
        } else {
            updateExistingInstance();
        }
        unsavedChanges.clear();
    }

    private String getKeys() {
        String result = "";
        for (String key : unsavedChanges.keySet() ) {
            if (result.length() > 0) {
                result += ",";
            }
            result += key;
        }
        return result;
    }

    private Timestamp getTimeStamp() {
        return new Timestamp(new Date().getTime() );
    }

    private synchronized void updateExistingInstance() {
        try {
            String sql = "UPDATE " + name + " SET (";
            sql += getKeys() + ") = (";

            for (int i = 1; i < unsavedChanges.size(); i++) {
                sql += "?,";
            }
            sql += "?) WHERE id = ?";
            PreparedStatement statement = base.connection.prepareStatement(sql);
            int count = 1;
            for (Object value : unsavedChanges.values() ) {
                statement.setObject(count, value);
                count += 1;
            }
            statement.setInt(count, id);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new DBException("Something went wrong within UPDATE_EXISTING_INSTANCE method of Model class!", e);
        }
    }

    private synchronized void insertNewInstance() {
        try {
            String sql = "INSERT INTO " + name + " (" + getKeys() + ") VALUES (";
            for (int i = 1; i < unsavedChanges.size(); i++) {
                sql += "?,";
            }
            sql += "?)";
            PreparedStatement statement = base.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int count = 1;
            for (Object value : unsavedChanges.values() ) {
                statement.setObject(count, value);
                count += 1;
            }

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next() ) {
                id = rs.getInt(1);
            }
        } catch (Exception e) {
            throw new DBException("Something went wrong within INSERT_NEW_INSTANCE method of Model class!", e);
        }
    }

}
