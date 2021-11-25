package etf.sni.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import etf.sni.beans.UserBean;
import etf.sni.controller.InputController;
import etf.sni.controller.RegistrationController;
import etf.sni.controller.SessionController;
import etf.sni.dto.User;

@WebServlet("/RegistrationServlet")
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024 * 1,
		maxFileSize = 1024 * 1024 * 10,
		maxRequestSize = 1024 * 1024 * 100
)
public class RegistrationServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
    public RegistrationServlet() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
    	request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession();
		if(SessionController.getSessionController().isSessionHijacking(request)) {
			SessionController.getSessionController().terminateSession(request, 3);
			response.sendRedirect("./DetectionServlet?action=sessionHijacking");
			return;
		}
		
		String address;
		String action = request.getParameter("action");

		if (action == null) {
			session.setAttribute("notification", "");
			address = "WEB-INF/pages/Registration.jsp";
		}
		else if (action.equals("showRegistration")) {
			address = "WEB-INF/pages/Registration.jsp";
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
		
		if (action.equals("signup")) {
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String email = request.getParameter("email");
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String password2 = request.getParameter("password2");

			if(firstName != null && lastName != null && email != null 
					&& username != null && password != null && password2 != null ) {
				
				//provjera sql injection i xss napada
				int value = InputController.getInputController().
						checkInput(Arrays.asList(firstName, lastName, email, username, password, password2));
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
				//onda je 0 i sve je u redu
				else {
					RegistrationController controller = RegistrationController.getRegistrationController();
					
					if(!controller.isUsernameUsed(username)) {
						if(!controller.isMailAddressUsed(email)) {
							
							if(!password.equals(password2)) {
								session.setAttribute("notification", "Lozinke se ne poklapaju.");
								response.sendRedirect("./RegistrationServlet?action=showRegistration");
							}
							else {
								if (controller.generateCertificate(username)) {
									
									User user = new User(username, email, password, firstName, lastName);
									if (controller.writeUserData(user)) {   
										
										if(controller.sendGeneratedCertificate(username)) {
											session.setAttribute("notification", "Registracija uspjesna. Poslali smo Vam sertifikat na mail. Mozete se prijaviti na sistem.");
											response.sendRedirect("LoginServlet?action=showLogin");
										}
										else {
											session.setAttribute("notification", "Doslo je do greske prilikom slanja sertifikata.");
											response.sendRedirect("./RegistrationServlet?action=showRegistration");
										}
									}
									else {
										session.setAttribute("notification", "Doslo je do greske prilikom upisivanja podataka u bazu.");
										response.sendRedirect("./RegistrationServlet?action=showRegistration");
									}
								}
								else {
									session.setAttribute("notification", "Doslo je do greske prilikom generisanja korisnickog sertifikata.");
									response.sendRedirect("./RegistrationServlet?action=showRegistration");
								}
							}
						}
						else {
							session.setAttribute("notification", "Vec postoji nalog sa unesenom mail adresom.");
							response.sendRedirect("./RegistrationServlet?action=showRegistration");
						}
					}
					else {
						session.setAttribute("notification", "Vec postoji nalog sa unesenim korisnickim imenom.");
						response.sendRedirect("./RegistrationServlet?action=showRegistration");
					}
				}
			}
		}
		else {
			response.sendRedirect("./DetectionServlet?action=pageNotFound");
		}
	
	}

}
