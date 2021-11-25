package etf.sni.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import etf.sni.dto.User;

public class UserDAO {
	private static final String SQL_IS_USERNAME_USED = 
			"SELECT * FROM user WHERE Username=?";
	private static final String SQL_IS_EMAIL_USED = 
			"SELECT * FROM user WHERE Email=?";
	private static final String SQL_INSERT_USER = 
			"INSERT INTO user (Id, Email, Username, FirstName, LastName, Password, Salt) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_IS_PASSWORD_VALID = 
			"{ CALL check_password(?, ?, ?) }";
	private static final String SQL_GET_EMAIL = 
			"SELECT Email FROM user WHERE Username=?";
	private static final String SQL_GET_USER_ID = 
			"SELECT Id FROM user WHERE Username=?";
	
	public static boolean isUsernameUsed(String username) {
		boolean isUsed = true;
		
		Connection c = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Object values[] = {username};
		
		try {
			c = DAOUtil.getConnection();
			ps = DAOUtil.prepareStatement(c, SQL_IS_USERNAME_USED, false, values);
			rs = ps.executeQuery();
			if(!rs.next()) {
				isUsed = false;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			DAOUtil.close(rs, ps, c);
		}
		
		return isUsed;
	}
	
	public static String getEmailByUsername(String username) {
		String email = "";
		
		Connection c = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Object values[] = {username};
		
		try {
			c = DAOUtil.getConnection();
			ps = DAOUtil.prepareStatement(c, SQL_GET_EMAIL, false, values);
			rs = ps.executeQuery();
			if(rs.next()) {
				email = rs.getString(1);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			DAOUtil.close(rs, ps, c);
		}
		
		return email;
	}
	
	static int getIdByUsername(String username) {
		int id = -1;
		
		Connection c = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Object values[] = {username};
		
		try {
			c = DAOUtil.getConnection();
			ps = DAOUtil.prepareStatement(c, SQL_GET_USER_ID, false, values);
			rs = ps.executeQuery();
			if(rs.next()) {
				id = rs.getInt(1);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			DAOUtil.close(rs, ps, c);
		}
		
		return id;
	}
	
	public static boolean isMailAddressUsed(String email) {
		boolean isUsed = true;
		
		Connection c = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Object values[] = {email};
		
		try {
			c = DAOUtil.getConnection();
			ps = DAOUtil.prepareStatement(c, SQL_IS_EMAIL_USED, false, values);
			rs = ps.executeQuery();
			if(!rs.next()) {
				isUsed = false;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			DAOUtil.close(rs, ps, c);
		}
		
		return isUsed;
	}
	
	public static boolean insertUser(User user) {
		boolean isInserted = false;
		
		Connection c = null;
		PreparedStatement ps = null;
		Object values[] = {null, user.getEmail(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getPassword(), null};
		
		try {
			c = DAOUtil.getConnection();
			ps = DAOUtil.prepareStatement(c, SQL_INSERT_USER, false, values);
			ps.executeUpdate();
			isInserted = true;
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			DAOUtil.close(ps, c);
		}
		
		return isInserted;
	}
	
	public static boolean isPasswordValid(String username, String password) {
		boolean isValid = false;
		int result = 1;
		
		Connection c = null;
		CallableStatement cs = null;
		Object values[] = {username, password};
		
		try {
			c = DAOUtil.getConnection();
			cs = DAOUtil.prepareCall(c, SQL_IS_PASSWORD_VALID, Types.INTEGER, values);
			cs.execute();
			
			result = cs.getInt(3);
			
			if(result == 0)
				isValid = true;

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			DAOUtil.close(cs, c);
		}
		
		return isValid;
	}
}
