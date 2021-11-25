package etf.sni.controller;

import java.security.cert.X509Certificate;
import java.util.Random;

import etf.sni.dao.UserDAO;

public class LoginController {

	private static LoginController loginController;
	
	private LoginController() {}
	
	public static LoginController getLoginController() {
		if(loginController == null) {
			loginController = new LoginController();
		}
		return loginController;
	}
	
	public boolean isUsernameValid(String username) {
		return UserDAO.isUsernameUsed(username);
	}
	
	public boolean isPasswordValid(String username, String password) {
		return UserDAO.isPasswordValid(username, password);
	}
	
	public boolean sendToken(String username, String token) {
		
		String email = UserDAO.getEmailByUsername(username);
		
		MailController mailController = MailController.getMailController();
		
		return mailController.sendMail(email, token);
	}
	
	public String generateToken() {
		String chars = "0123456789";
		Random random = new Random();
		
		StringBuilder stringBuilder = new StringBuilder(6);
		for(int i = 0; i < 6; i++) {
			int position = random.nextInt(chars.length());
		    stringBuilder.append(chars.charAt(position));
		}
		return stringBuilder.toString();
	}
	
	public boolean ValidateCertificate(String username, String filePath) {
		boolean valid = false;
		try {
			X509Certificate cert = RegistrationController.getRegistrationController().loadCertificate(filePath);
			
			cert.checkValidity(); //bacice izuzetak ako nije validan datum
			
			if (!cert.getSubjectX500Principal().getName().toString().equals("CN=" + username))
				return false;

			if (!cert.getIssuerX500Principal().getName().toString().equals("CN=SecureChatApp"))
				return false;
			
			valid = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return valid;
	}
	
}
