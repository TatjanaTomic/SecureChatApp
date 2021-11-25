package etf.sni.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

//Using Singleton pattern
public class ConnectionPool {
	
	private String jdbcURL;
	private String username;
	private String password;
	private String driver;
	private int preconnectCount;
	private int maxIdleConnections;
	private int maxConnections;

	private int connectCount;
	private List<Connection> usedConnections;
	private List<Connection> freeConnections;

	private static ConnectionPool connectionPool;
	
	public static ConnectionPool getConnectionPool() {
		if(connectionPool == null)
			connectionPool = new ConnectionPool();
		return connectionPool;
	}
	
	private ConnectionPool() {
		readConfiguration();
		try {
			freeConnections = new ArrayList<Connection>();
			usedConnections = new ArrayList<Connection>();
			
			for (int i = 0; i < preconnectCount; i++) {
				Connection connection = DriverManager.getConnection(jdbcURL, username, password);
				freeConnections.add(connection);
			}
			
			connectCount = preconnectCount;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//reading database.properties file
	private void readConfiguration() {
		ResourceBundle bundle = PropertyResourceBundle.getBundle("etf.sni.dao.database");
		jdbcURL = bundle.getString("jdbcURL");
		username = bundle.getString("username");
		password = bundle.getString("password");
		driver = bundle.getString("driver");
		preconnectCount = 0;
		maxIdleConnections = 10;
		maxConnections = 10;
		try {
			preconnectCount = Integer.parseInt(bundle.getString("preconnectCount"));
			maxIdleConnections = Integer.parseInt(bundle.getString("maxIdleConnections"));
			maxConnections = Integer.parseInt(bundle.getString("maxConnections"));
			Class.forName(driver); //Ova linija koda nije potrebna kad se drajver za komunikaciju sa bazom podataka doda pravilno, tj. pomocu classpath-a
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public synchronized Connection checkOut() throws SQLException {
		Connection connection = null;
		
		if (freeConnections.size() > 0) {
			connection = freeConnections.remove(0);
			usedConnections.add(connection);
		} 
		else {
			if (connectCount < maxConnections) {
				connection = DriverManager.getConnection(jdbcURL, username, password);
				usedConnections.add(connection);
				connectCount++;
			}
			else {
				try {
					wait();
					connection = freeConnections.remove(0);
					usedConnections.add(connection);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return connection;
	}
	
	public synchronized void checkIn(Connection connection) {
		if(connection == null)
			return;
		
		if(usedConnections.remove(connection)) {
			freeConnections.add(connection);
			while (freeConnections.size() > maxIdleConnections) {
				int last = freeConnections.size() - 1;
				Connection conn = freeConnections.remove(last);
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			notify();
		}
	}
		
}
