package etf.sni.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import etf.sni.dto.Message;

public class MessageDAO {
	private static final String SQL_INSERT_MESSAGE = 
			"INSERT INTO message VALUES (null, current_timestamp(), ?, ?, ?);";
	private static final String SQL_GET_MESSAGES = 
			"SELECT * FROM conversation WHERE (SenderId=? and ReceiverId=?) or (SenderId=? and ReceiverId=?) order by DateTime;";
	
	public static boolean insertMessage(Message message) {
		boolean isInserted = false;
		
		int senderId = UserDAO.getIdByUsername(message.getSender());
		int receiverId = UserDAO.getIdByUsername(message.getReceiver());
		
		if(senderId != -1 && receiverId != -1) {
			Connection c = null;
			PreparedStatement ps = null;
			Object values[] = {message.getContent(), senderId, receiverId};
			
			try {
				c = DAOUtil.getConnection();
				ps = DAOUtil.prepareStatement(c, SQL_INSERT_MESSAGE, false, values);
				ps.executeUpdate();
				isInserted = true;
				
			} catch(SQLException e) {
				e.printStackTrace();
			} finally {
				DAOUtil.close(ps, c);
			}
			
		}
		
		return isInserted;
	}
	
	public static List<Message> getMessages(String user1, String user2) {
		List<Message> messages = new ArrayList<>();
		
		int idUser1 = UserDAO.getIdByUsername(user1);
		int idUser2 = UserDAO.getIdByUsername(user2);
		
		if(idUser1 != -1 && idUser2 != -1) {
			Connection c = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			Object values[] = {idUser1, idUser2, idUser2, idUser1};
			
			try {
				c = DAOUtil.getConnection();
				ps = DAOUtil.prepareStatement(c, SQL_GET_MESSAGES, false, values);
				rs = ps.executeQuery();
				
				while (rs.next()) {
					Message message = new Message(rs.getString(3), rs.getTimestamp(2).toString(), rs.getString(5), rs.getString(7));
					messages.add(message);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			} finally {
				DAOUtil.close(rs, ps, c);
			}
		}
		
		return messages;
	}
	
	
	
}
