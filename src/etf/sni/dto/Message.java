package etf.sni.dto;

import java.io.Serializable;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3751805729761026573L;
	
	private String sender;
	private String receiver;
	private String dateTime;
	private String content;
	
	public Message(String content) {
		super();
		this.content = content;
	}
	
	public Message(String content, String sender, String receiver) {
		super();
		this.content = content;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public Message(String content, String dateTime, String sender, String receiver) {
		super();
		this.content = content;
		this.dateTime = dateTime;
		this.sender = sender;
		this.receiver = receiver;
	}

	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getReceiver() {
		return receiver;
	}
	
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public String getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
}
