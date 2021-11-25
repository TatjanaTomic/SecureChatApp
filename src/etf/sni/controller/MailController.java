package etf.sni.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailController {
	
	private Properties props = new Properties();
	
	private static final String TITLE_TOKEN = "Token";
	private static final String TITLE_CERT = "Certificate";
	private static final String SENDER_NAME = "SecureChatApp";
	
	private static String serverMail = "";
	private static String serverPass = "";
	private static final String path = "C:\\Users\\EC\\Desktop\\server.txt";
	
	private static MailController mailController;
	
	
	private MailController() {
		loadConfiguration();
		readServerCredentials();
	}
	
	public static MailController getMailController() {
		if(mailController == null) 
			mailController = new MailController();
		return mailController;
	}
	
	private void readServerCredentials() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));

			serverMail = br.readLine();
			serverPass = br.readLine();
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Properties loadConfiguration() {
		try {
			props.load(getClass().getResourceAsStream("mail.properties"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
	
	public boolean sendMailWithAttachment(String receiver, String fileName, String filePath) {
		
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(serverMail, serverPass);
			}
		});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(serverMail, SENDER_NAME));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
			message.setSubject(TITLE_CERT);
			
			BodyPart messageBodyPart1 = new MimeBodyPart();
			messageBodyPart1.setText("Client's certificate");
			
			BodyPart messageBodyPart2 = new MimeBodyPart();
			DataSource source = new FileDataSource(filePath);
			messageBodyPart2.setDataHandler(new DataHandler(source));
			messageBodyPart2.setFileName(fileName);
			
			Multipart multipart = new MimeMultipart();  
		    multipart.addBodyPart(messageBodyPart1);  
		    multipart.addBodyPart(messageBodyPart2);  
		  
		    message.setContent(multipart);  
		
			Transport.send(message);
			
			return true;
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return false;
		}
		
	}
	
	public boolean sendMail(String receiver, String content) {
		
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(serverMail, serverPass);
			}
		});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(serverMail, SENDER_NAME));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
			message.setSubject(TITLE_TOKEN);
			message.setText(content);
			
			Transport.send(message);
			
			return true;
		} catch (MessagingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			
			return false;
		}
		
	}
	

		
}
