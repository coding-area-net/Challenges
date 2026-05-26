package net.codingarea.commons.database;

import lombok.Getter;
import net.codingarea.commons.common.config.Propertyable;

import javax.annotation.Nonnull;

public final class DatabaseConfig {

	@Getter
    private final String host;
	@Getter
    private final String database;
	@Getter
    private final String authDatabase;
	@Getter
    private final String password;
	@Getter
    private final String user;
	@Getter
    private final String file;
	@Getter
    private final int port;
	private final boolean portIsSet;

	public DatabaseConfig(String host, String database, String password, String user, int port) {
		this(host, database, null, password, user, port, true, null);
	}
	public DatabaseConfig(String host, String database, String password, String user) {
		this(host, database, null, password, user, 0, false, null);
	}

	public DatabaseConfig(String host, String database, String authDatabase, String password, String user, int port) {
		this(host, database, authDatabase, password, user, port, true, null);
	}
	public DatabaseConfig(String host, String database, String authDatabase, String password, String user) {
		this(host, database, authDatabase, password, user, 0, false, null);
	}

	public DatabaseConfig(String database, String file) {
		this(null, database, null, null, null, 0, false, file);
	}

	public DatabaseConfig(String host, String database, String authDatabase, String password, String user, int port, boolean portIsSet, String file) {
		this.host = host;
		this.database = database;
		this.authDatabase = authDatabase;
		this.password = password;
		this.user = user;
		this.port = port;
		this.portIsSet = portIsSet;
		this.file = file;
	}

	public DatabaseConfig(@Nonnull Propertyable config) {
		this(
				config.getString("host"),
				config.getString("database"),
				config.getString("auth-database"),
				config.getString("password"),
				config.getString("user"),
				config.getInt("port"),
				config.contains("port"),
				config.getString("file")
		);
	}

    public boolean isPortSet() {
		return portIsSet;
	}

    @Override
	public String toString() {
		return "DatabaseConfig{" +
				"host='" + host + '\'' +
				", database='" + database + '\'' +
				", authDatabase='" + authDatabase + '\'' +
				", user='" + user + '\'' +
				", file='" + file + '\'' +
				", port=" + port +
				", portIsSet=" + portIsSet +
				'}';
	}

}
