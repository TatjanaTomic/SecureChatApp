package etf.sni.beans;

import java.io.Serializable;
import java.util.List;

import etf.sni.controller.MessagesController;
import etf.sni.dto.Message;

public class ConversationBean implements Serializable {

	private static final long serialVersionUID = -8082047542941999465L;
	
	private String user1;
	private String user2;
	private List<Message> messages;
	
	public ConversationBean() {}
	
	public ConversationBean(String user1, String user2) {
		this.user1 = user1;
		this.user2 = user2;
		this.messages = MessagesController.getMessagesController().getAllMessages(user1, user2);
	}

	public String getUser1() {
		return user1;
	}

	public void setUser1(String sender) {
		this.user1 = sender;
	}

	public String getUser2() {
		return user2;
	}

	public void setUser2(String receiver) {
		this.user2 = receiver;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}
