package com.i0dev.managers;

import com.i0dev.Bot;
import com.i0dev.DiscordBot;
import com.i0dev.config.GeneralConfig;
import com.i0dev.utility.LogUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;

@Getter
@Setter
public class SQLManager extends Manager {

    Connection connection;

    public SQLManager(DiscordBot bot) {
        super(bot);
    }

    @Override
    public void initialize() {
        if (connection != null) deinitialize();
        connect();
    }

    @SneakyThrows
    @Override
    public void deinitialize() {
        connection.close();
        connection = null;
    }


    @SneakyThrows
    public void connect() {
        Class.forName("org.sqlite.JDBC");
        String database = GeneralConfig.get().getDbName();
        if (GeneralConfig.get().isUseDatabase()) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + GeneralConfig.get().getDbAddress() + ":" + GeneralConfig.get().getDbPort() + "/" + database);
            config.setUsername(GeneralConfig.get().getDbUsername());
            config.setPassword(GeneralConfig.get().getDbPassword());
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            HikariDataSource ds = new HikariDataSource(config);
            connection = ds.getConnection();
            System.out.println("Connected to Hikari MySQL server database: " + database);
        } else {
            if (Bot.getBot().isPluginMode()) database = com.i0dev.BotPlugin.get().getDataFolder() + "/DiscordBot.db";
            else database = "DiscordBot/DiscordBot.db";
            String url = "jdbc:sqlite:" + database;
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite local database: " + database);
        }
    }

    @SneakyThrows
    public void absenceCheck(Class<?> clazz) {
        String table = clazz.getSimpleName();
        List<String> columns = getColumns(table);

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            if (columns.contains(field.getName())) continue;
            String type = "";
            switch (field.getType().getName()) {
                case "java.lang.Long":
                case "long":
                    type = "BIGINT NOT NULL DEFAULT 0";
                    break;
                case "java.lang.Double":
                case "double":
                    type = "DOUBLE(16,10) NOT NULL DEFAULT 0";
                    break;
                case "java.lang.String":
                    type = "VARCHAR(300) NOT NULL DEFAULT 0";
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    type = "BIT NOT NULL DEFAULT 0";
                    break;
                default:
                    return;
            }
            LogUtil.debug("Adding column [" + field.getName() + "] to " + table + " during an absence check.");
            String query = "ALTER TABLE " + table + " ADD COLUMN " + field.getName() + " " + type + ";";
            Statement statement = connection.createStatement();
            statement.execute(query);
        }

        columns = getColumns(table);

        List<String> fields = new ArrayList<>();
        Arrays.stream(clazz.getDeclaredFields()).filter(field -> !Modifier.isTransient(field.getModifiers())).forEach(field -> fields.add(field.getName()));
        for (String column : columns) {
            if (fields.contains(column)) continue;
            LogUtil.debug("Removing column [" + column + "] from " + table + " during an absence check.");
            String query = "ALTER TABLE " + table + " DROP COLUMN " + column + ";";
            Statement statement = connection.createStatement();
            statement.execute(query);
        }
    }

    public List<String> getColumns(String table) throws SQLException {
        if (GeneralConfig.get().isUseDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SHOW COLUMNS FROM " + table + ";");
            ResultSet set = preparedStatement.executeQuery();
            List<String> columns = new ArrayList<>();
            while (set.next()) {
                columns.add(set.getString("field"));
            }
            return columns;
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement("PRAGMA table_info(" + table + ");");
            ResultSet set = preparedStatement.executeQuery();
            List<String> columns = new ArrayList<>();
            while (set.next()) {
                columns.add(set.getString("name"));
            }
            return columns;
        }
    }

    @SneakyThrows
    public void makeTable(Class<?> clazz) {
        String name = clazz.getSimpleName();
        List<String> list = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(declaredField.getModifiers())) continue;
            list.addAll(getColumnLines(declaredField));
        }
        int lastIndex = list.size() - 1;
        String lastItem = list.get(lastIndex);
        list.remove(lastIndex);
        list.add(lastItem.substring(0, lastItem.length() - 1));
        StringBuilder toQ = new StringBuilder();
        for (String s : list) {
            toQ.append(s);
        }
        String query = "CREATE TABLE IF NOT EXISTS " + name + " (" + toQ + ")";
        connection.prepareStatement(query).execute();
        absenceCheck(clazz);
    }

    public List<String> getColumnLines(Field field) {
        String type = field.getType().getTypeName();
        String name = field.getName();
        List<String> ret = new ArrayList<>();
        switch (type) {
            case "java.lang.Long":
            case "long":
                ret.add("`" + name + "` BIGINT NOT NULL DEFAULT 0,");
                break;
            case "java.lang.Double":
            case "double":
                ret.add("`" + name + "` DOUBLE(16,10) NOT NULL DEFAULT 0,");
                break;
            case "java.lang.String":
                ret.add("`" + name + "` VARCHAR(300) NOT NULL DEFAULT '',");
                break;
            case "java.lang.Boolean":
            case "boolean":
                ret.add("`" + name + "` BIT NOT NULL DEFAULT 0,");
                break;
        }
        return ret;
    }

    @SneakyThrows
    public void updateTable(Object object, String key, String value) {
        if (!objectExists(object.getClass().getSimpleName(), key, value)) {
            LogUtil.debug("SQL pair: [" + key + ", " + value + "] did not exist. Creating now...");
            insertToTable(object);
            return;
        }

        Class<?> clazz = object.getClass();
        StringBuilder toQ = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            String type = field.getType().getTypeName();
            switch (type) {
                case "java.lang.Long":
                case "long":
                    toQ.append(field.getName()).append(" = ").append(field.getLong(object)).append(",");
                    break;
                case "java.lang.Double":
                case "double":
                    toQ.append(field.getName()).append(" = ").append(field.getDouble(object)).append(",");
                    break;
                case "java.lang.String":
                    toQ.append(field.getName()).append(" = ").append("'").append(field.get(object)).append("',");
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    toQ.append(field.getName()).append(" = ").append((field.getBoolean(object) ? 1 : 0)).append(",");
                    break;
            }
        }
        String query = "UPDATE " + clazz.getSimpleName() + " SET " + toQ.substring(0, toQ.length() - 1) + " " +
                "WHERE " + key + " = " + value + ";";
        connection.prepareStatement(query).execute();
    }

    @SneakyThrows
    public void updateAll(String table, String key, Object value) {
        switch (value.getClass().getTypeName()) {
            case "java.lang.Long":
            case "java.lang.Double":
                break;
            case "java.lang.String":
                value = "'" + value + "'";
                break;
            case "java.lang.Boolean":
                value = (Boolean) value ? 1 : 0;
                break;
        }
        LogUtil.debug("SQL Updated all values for: [" + value + "], in the table [" + table + "]. Set all values to: [" + value + "]");
        String query = "UPDATE " + table + " SET " + key + " = " + value;
        connection.prepareStatement(query).execute();
    }

    @SneakyThrows
    public List<Object> getAllObjects(String table, String key, Class<?> clazz) {
        List<Object> ret = new ArrayList<>();
        String query = "SELECT * FROM " + table + ";";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        while (resultSet.next()) {
            String value = resultSet.getString(key);
            ret.add(getObject(key, value, clazz));
        }
        return ret;
    }

    @SneakyThrows
    public List<Object> getAllObjectsWhereValuesNot(String table, String key, Class<?> clazz, String notKey, Collection<String> notValues) {
        List<Object> ret = new ArrayList<>();
        StringBuilder all = new StringBuilder();
        for (String notValue : notValues) {
            all.append(notValue).append(", ");
        }
        String allS = "";
        if (!"".equals(all.toString())) allS = all.substring(0, all.length() - 2);
        String query = "SELECT * FROM " + table + " where " + notKey + " NOT IN (" + allS + ");";
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        while (resultSet.next()) {
            String value = resultSet.getString(key);
            ret.add(getObject(key, value, clazz));
        }
        return ret;
    }

    @SneakyThrows
    public boolean objectExists(String table, String key, String value) {
        String query = "SELECT * FROM " + table + " WHERE " + key + " = " + value;
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        return resultSet.next();
    }

    @SneakyThrows
    public void clearTable(String table) {
        String query = "DELETE FROM " + table;
        connection.prepareStatement(query).execute();
    }

    @SneakyThrows
    public void deleteFromTable(String table, String key, String value) {
        String query = "DELETE FROM " + table + " WHERE " + key + "=" + value;
        connection.prepareStatement(query).execute();
    }

    @SneakyThrows
    public void insertToTable(Object object) {
        Class<?> clazz = object.getClass();
        StringBuilder toQ = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            String type = field.getType().getTypeName();
            switch (type) {
                case "java.lang.Long":
                case "long":
                    toQ.append(field.getLong(object)).append(",");
                    break;
                case "java.lang.Double":
                case "double":
                    toQ.append(field.getDouble(object)).append(",");
                    break;
                case "java.lang.String":
                    toQ.append("'").append(field.get(object)).append("',");
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    toQ.append((field.getBoolean(object) ? 1 : 0)).append(",");
                    break;
            }
        }
        connection.createStatement().execute("INSERT INTO " + clazz.getSimpleName() + " VALUES(" + toQ.substring(0, toQ.length() - 1) + ");");
    }

    @SneakyThrows
    public Object getObject(String key, Object value, Class<?> clazz) {
        String val;
        switch (value.getClass().getSimpleName()) {
            case "long":
            case "Long":
                val = ((Long) value).toString();
                break;
            case "String":
                val = "'" + value + "'";
                break;
            case "boolean":
                val = (Boolean) value ? "1" : "0";
                break;
            default:
                val = value.toString();
        }
        if (!objectExists(clazz.getSimpleName(), key, val)) return null;
        ResultSet result = connection.createStatement().executeQuery("select * from " + clazz.getSimpleName() + " where " + key + "=" + val + ";");
        int iter = 0;
        result.next();
        Object ret = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            iter++;
            String type = field.getType().getTypeName();
            switch (type) {
                case "java.lang.Long":
                case "long":
                    field.setLong(ret, result.getLong(field.getName()));
                    break;
                case "java.lang.String":
                    field.set(ret, result.getString(field.getName()));
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    field.setBoolean(ret, result.getBoolean(field.getName()));
                    break;
                case "java.lang.Double":
                case "double":
                    field.setDouble(ret, result.getDouble(field.getName()));
                    break;
            }
        }
        result.close();
        return ret;
    }

    @SneakyThrows
    public List<Object> getListWhere(String table, String key, String value, Class<?> castTo, String objectKey) {
        List<Object> ret = new ArrayList<>();
        String query = "SELECT * FROM " + table + " WHERE " + key + "=" + value;
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        while (resultSet.next()) {
            ret.add(getObject(objectKey, resultSet.getString(objectKey), castTo));
        }
        return ret;
    }

    @SneakyThrows
    public List<Object> getListWhereNot(String table, String key, String value, Class<?> castTo, String objectKey) {
        List<Object> ret = new ArrayList<>();
        String query = "SELECT * FROM " + table + " WHERE " + key + "!=" + value;
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        while (resultSet.next()) {
            ret.add(getObject(objectKey, resultSet.getString(objectKey), castTo));
        }
        return ret;
    }

    @SneakyThrows
    public List<Object> getSortedList(String table, String orderBy, Class<?> castTo, int limit, String key) {
        List<Object> ret = new ArrayList<>();
        String query = "SELECT * FROM " + table + " ORDER BY " + orderBy + " DESC LIMIT " + limit;
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        while (resultSet.next()) {
            ret.add(getObject(key, resultSet.getString(key), castTo));
        }
        return ret;
    }

}
