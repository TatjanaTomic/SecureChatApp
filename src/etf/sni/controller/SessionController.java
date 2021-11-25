package etf.sni.controller;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import etf.sni.dto.User;
import etf.sni.servlets.ChatServlet;

public class SessionController {
	
	private HashMap<String, String> sessionsMap;
	
	private static SessionController sessionController;
	
	private static final String LOGFILE_PATH = "C:\\Users\\EC\\eclipse-workspace-2021-09\\SecureChatApp\\log.txt";
	
	private SessionController() {
		sessionsMap = new HashMap<>();
	}
	
	public static SessionController getSessionController() {
		if(sessionController == null)
			sessionController = new SessionController();
		return sessionController;
	}
	
	public void closeSession(String sessionId) {
		if(sessionsMap.containsKey(sessionId)) {
			sessionsMap.remove(sessionId);
		}
	}
	
	public void saveNewSession(HttpServletRequest request) {
		
		String ipAddress = getIpAddress(request);
		String sessionId;
		
		if(readJsessionidFromCookie(request).isPresent()) {
			
			sessionId = readJsessionidFromCookie(request).get();
			if(sessionsMap.containsKey(sessionId)) {
				sessionsMap.remove(sessionId);
			}
			
			sessionsMap.put(sessionId, ipAddress);
		}
	}	
	
	/* We can get all cookies by calling getCookies() on the request 
	 * (HttpServletRequest) passed to the Servlet.
	 * We can iterate through this array and search for the one we need */
	
	public Optional<String> readJsessionidFromCookie(HttpServletRequest request) {
		String key = "JSESSIONID";
		Optional<String> result = Optional.empty();
		
		if(request.getCookies() != null) {
			try {
				result = Arrays.stream(request.getCookies())
		    	      .filter(c -> key.equals(c.getName()))
		    	      .map(Cookie::getValue)
		    	      .findAny();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public boolean isSessionHijacking(HttpServletRequest request) {
		boolean isSessionHijacking = false;
		
		try {
			String ipAddress = getIpAddress(request);
			String sessionId;
			
			if(readJsessionidFromCookie(request).isPresent()) {
				
				sessionId = readJsessionidFromCookie(request).get();
				
				if(!sessionsMap.containsKey(sessionId)) {
					sessionsMap.put(sessionId, ipAddress);
				}
				else if (!sessionsMap.get(sessionId).equals(ipAddress)) {
					isSessionHijacking = true;	
				}
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSessionHijacking;
	}
	
	private String getIpAddress(HttpServletRequest request) {
		String address = request.getHeader("X-FORWARDED-FOR");
		if (address == null) {
			address = request.getRemoteAddr();
		}
		return address;
	}
	
	public void terminateSession(HttpServletRequest request, int attack) {
		
		String ipAddress = getIpAddress(request);
		String sessionId = readJsessionidFromCookie(request).get();
		
		if(attack != 0) {
			logAttack(ipAddress, attack);
			System.out.println("OK");
		}
		
		if(request.getSession().getAttribute("user") != null) {
			HttpSession session = request.getSession();
			User currentUser = (User) session.getAttribute("user");
			ChatServlet.onlineUsers.logoutUser(currentUser);
			session.removeAttribute("user");
		}
		
		if (sessionId != null) {
			sessionsMap.remove(sessionId);
		}
		
		request.getSession().invalidate();
		
	}
	
	// 1 - SqlInjection
	// 2 - XSS
	// 3 - SessionHijacking
	private void logAttack(String ipAddress, int attack) {
		String detectedAttack = "";
		if(attack == 1)
			detectedAttack = "Detektovan pokusaj SqlInjection napada";
		else if(attack == 2)
			detectedAttack = "Detektovan pokusaj XSS napada";
		else if(attack == 3) 
			detectedAttack = "Detektovana je kradja sesije";
		
		try {
			File logFile = new File(LOGFILE_PATH);
			
			FileWriter fw = new FileWriter(logFile, true);
		    fw.write(ipAddress + " : " + detectedAttack + "\n");
		    fw.close();
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
}
