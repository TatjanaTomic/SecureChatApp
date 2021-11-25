package etf.sni.controller;

import java.util.List;

import etf.sni.dao.MessageDAO;
import etf.sni.dto.Message;

public class MessagesController {

	private static MessagesController messagesController;
	
	private MessagesController() {}

	public static MessagesController getMessagesController() {
		if(messagesController == null) {
			messagesController = new MessagesController();
		}
		return messagesController;
	}
	
	public boolean saveNewMessage(String content, String sender, String receiver) {
		return MessageDAO.insertMessage(new Message(content, sender, receiver));
	}
	
	public List<Message> getAllMessages(String user1, String user2) {
		return MessageDAO.getMessages(user1, user2);
	}
}
