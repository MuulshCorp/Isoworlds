package common;

/**
 * Created by Edwin on 17/10/2017.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Mysql {
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private boolean autoReconnect;
    private Connection connection;

    public Mysql(String host, int port, String database, String username, String password, boolean autoReconnect) {
        setHost(host);
        setPort(port);
        setDatabase(database);
        setUsername(username);
        setPassword(password);
        setAutoReconnect(autoReconnect);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof common.Mysql)) {
            return false;
        }
        common.Mysql other = (common.Mysql) o;
        if (!other.canEqual(this)) {
            return false;
        }
        Object this$host = getHost();
        Object other$host = other.getHost();
        if (this$host == null ? other$host != null : !this$host.equals(other$host)) {
            return false;
        }
        if (getPort() != other.getPort()) {
            return false;
        }
        Object this$database = getDatabase();
        Object other$database = other.getDatabase();
        if (this$database == null ? other$database != null : !this$database.equals(other$database)) {
            return false;
        }
        Object this$username = getUsername();
        Object other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        Object this$password = getPassword();
        Object other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
            return false;
        }
        if (isAutoReconnect() != other.isAutoReconnect()) {
            return false;
        }
        Object this$connection = getConnection();
        Object other$connection = other.getConnection();
        return this$connection == null ? other$connection == null : this$connection.equals(other$connection);
    }

    protected boolean canEqual(Object other) {
        return other instanceof common.Mysql;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $host = getHost();
        result = result * 59 + ($host == null ? 43 : $host.hashCode());
        result = result * 59 + getPort();
        Object $database = getDatabase();
        result = result * 59 + ($database == null ? 43 : $database.hashCode());
        Object $username = getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        Object $password = getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        result = result * 59 + (isAutoReconnect() ? 79 : 97);
        Object $connection = getConnection();
        result = result * 59 + ($connection == null ? 43 : $connection.hashCode());
        return result;
    }

    public String toString() {
        return "Mysql(host=" + getHost() + ", port=" + getPort() + ", database=" + getDatabase() + ", username=" + getUsername() + ", password=" + getPassword() + ", autoReconnect=" + isAutoReconnect() + ", connection=" + getConnection() + ")";
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isAutoReconnect() {
        return this.autoReconnect;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        this.connection = DriverManager.getConnection("jdbc:Mysql://" + getHost() + ":" + getPort() + "/" + getDatabase() + "?useUnicode=true&characterEncoding=UTF-8&autoReconnect=" + Boolean.toString(isAutoReconnect()), getUsername(), getPassword());
    }

    public Statement query(String query)
            throws SQLException {
        Statement statement = getConnection().createStatement();
        return statement;
    }

    public PreparedStatement prepare(String query)
            throws SQLException {
        PreparedStatement state = getConnection().prepareStatement(query);
        return state;
    }
}