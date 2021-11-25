package etf.sni.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import etf.sni.controller.InputController;
import etf.sni.controller.LoginController;
import etf.sni.controller.SessionController;
import etf.sni.dto.User;


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public LoginServlet() {
        super();
    }

    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		if(SessionController.getSessionController().isSessionHijacking(request)) {
			SessionController.getSessionController().terminateSession(request, 3);
			response.sendRedirect("./DetectionServlet?action=sessionHijacking");
			return;
		}
		
		String address;
		String action = request.getParameter("action");
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		
		if (action == null) {
			session.setAttribute("notification", "");
			address = "WEB-INF/pages/Login.jsp";
		}
		else if (username == null || action.equals("showLogin")) {
			address = "WEB-INF/pages/Login.jsp";
		}
		else if (action.equals("showCertPage")) {
			address = "WEB-INF/pages/Certificate.jsp";
		}
		else if (action.equals("showTokenPage")) {
			address = "WEB-INF/pages/Token.jsp";
		}
		else {
			address = "WEB-INF/pages/404.jsp";
		}
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		if(SessionController.getSessionController().isSessionHijacking(request)) {
			SessionController.getSessionController().terminateSession(request, 3);
			response.sendRedirect("./DetectionServlet?action=sessionHijacking");
			return;
		}
		
		String action = request.getParameter("action");
		
		HttpSession session = request.getSession();
		session.setAttribute("notification", "");
		
		if (action.equals("login"))	{
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			if(username != null && password != null) {
				
				int value = InputController.getInputController().checkInput(Arrays.asList(username, password));
				if (value == 1) {
					response.sendRedirect("./DetectionServlet?action=sqlInjectionAttack");
					SessionController.getSessionController().terminateSession(request, 1);
					return;
				}
				else if (value == 2) {
					response.sendRedirect("./DetectionServlet?action=xssAttack");
					SessionController.getSessionController().terminateSession(request, 2);
					return;
				}
				else {
					LoginController loginController = LoginController.getLoginController();
					
					if(!loginController.isUsernameValid(username)) {
						session.setAttribute("notification", "Pogresno korisnicko ime.");
						response.sendRedirect("./LoginServlet?action=showLogin");
						return;
					} 
					else {
						if(!loginController.isPasswordValid(username, password)) {
							session.setAttribute("notification", "Pogresna lozinka.");
							response.sendRedirect("./LoginServlet?action=showLogin");
							return;
						}
						else {
							session.setAttribute("username", username);
							response.sendRedirect("./LoginServlet?action=showCertPage");
							return;
						}
					}
				}
			}
		}
		else if(action.equals("uploadCert")) {
			
			boolean uploaded = false;
			String UPLOAD_DIRECTORY = "C:\\Users\\EC\\Desktop\\upload";
			String username = (String) session.getAttribute("username");
			String filePath = UPLOAD_DIRECTORY + "\\" + username + ".pfx";
			
			try {
				org.apache.commons.fileupload.servlet.ServletFileUpload sf = 
						new org.apache.commons.fileupload.servlet.ServletFileUpload(
								new DiskFileItemFactory());
				
				List<FileItem> multiparts = sf.parseRequest(request);
				
				for (FileItem item : multiparts) {
					item.write(new File(filePath));
				}
				
				uploaded = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(uploaded) {
				LoginController loginController = LoginController.getLoginController();
				
				if(!loginController.ValidateCertificate(username, filePath)) {
					session.setAttribute("notification", "Prilozen je pogresan sertifikat. Prijavite se ponovo.");
					session.removeAttribute("username");
					response.sendRedirect("./LoginServlet?action=showLogin");
					return;
				}
				else {
					String token = loginController.generateToken();
					
					if(loginController.sendToken(username, token)) {
						session.setAttribute("token", token);
						response.sendRedirect("./LoginServlet?action=showTokenPage");
						return;
					}
					else {
						session.setAttribute("notification", "Doslo je do greske prilikom slanja tokena.");
						response.sendRedirect("./LoginServlet?action=showLogin");
						return;
					}
				}
			}
			else {
				session.setAttribute("notification", "Doslo je do greske prilikom upload-a sertifikata. Prijavite se ponovo.");
				session.removeAttribute("username");
				response.sendRedirect("./LoginServlet?action=showLogin");
				return;
			}
		}
		else if (action.equals("checkToken")) {
			String token = request.getParameter("token");
			
			if(token != null) {
				
				int value = InputController.getInputController().checkInput(Arrays.asList(token));
				if (value == 1) {
					response.sendRedirect("./DetectionServlet?action=sqlInjectionAttack");
					SessionController.getSessionController().terminateSession(request, 1);
					return;
				}
				else if (value == 2) {
					response.sendRedirect("./DetectionServlet?action=xssAttack");
					SessionController.getSessionController().terminateSession(request, 2);
					return;
				}
				else {

					String sentToken = (String) session.getAttribute("token");
					String username = (String) session.getAttribute("username");
					
					if(!token.equals(sentToken)) {
						session.setAttribute("notification", "Pogresan token. Pokusajte ponovo.");
						
						response.sendRedirect("./LoginServlet?action=showTokenPage");
						return;
					}
					else {
						
						session = request.getSession(false);
						if (session!=null) {
						    SessionController.getSessionController().terminateSession(request, 0);
						}
						
						session = request.getSession(true); // kreira novu sesiju
						SessionController.getSessionController().saveNewSession(request);
						
						User currentUser = new User(username);
						session.setAttribute("user", currentUser);
						
						response.sendRedirect("./ChatServlet?action=showOnlineUsers");
						return;
					}
				}
			}
		}
		else {
			response.sendRedirect("./DetectionServlet?action=pageNotFound");
		}
		
	}

}
