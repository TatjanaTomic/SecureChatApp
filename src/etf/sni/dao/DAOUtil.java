package etf.sni.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DAOUtil {

	private static ConnectionPool connectionPool = ConnectionPool.getConnectionPool();
	
	public static PreparedStatement prepareStatement(Connection connection, String sql, boolean returnGeneratedKeys, Object... values) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
		for (int i = 0; i < values.length; i++) {
			preparedStatement.setObject(i + 1, values[i]);
		}
		
		return preparedStatement;
	}
	
	public static CallableStatement prepareCall(Connection connection, String sql, int outputType, Object... values) throws SQLException {
		CallableStatement callableStatement = connection.prepareCall(sql);
		for (int i = 0; i < values.length; i++) {
			callableStatement.setObject(i + 1, values[i]);
		}
		callableStatement.registerOutParameter(values.length + 1, outputType);
		
		return callableStatement;
	}
	
	public static Connection getConnection() throws SQLException {
		Connection c = connectionPool.checkOut();
		return c;
	}
	
	public static void close(Connection c) {
		if(c!=null)
			connectionPool.checkIn(c);
	}
	
	public static void close(Statement s) {
		if (s != null) {
			try {
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void close(ResultSet rs, Statement s, Connection c) {
		close(rs);
		close(s);
		close(c);
	}

	public static void close(Statement s, Connection c) {
		close(s);
		close(c);
	}
		
}
