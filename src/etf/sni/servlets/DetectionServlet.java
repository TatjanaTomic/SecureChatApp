package etf.sni.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import etf.sni.controller.SessionController;

@WebServlet("/DetectionServlet")
public class DetectionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
       
    public DetectionServlet() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String address = "WEB-INF/pages/404.jsp";
		String action = request.getParameter("action");

		if(action == null || action.equals("pageNotFound")) {
			address = "WEB-INF/pages/404.jsp";
		}
		else if(action.equals("sqlInjectionAttack")) {
			address = "WEB-INF/pages/SqlInjection.jsp";
		}
		else if(action.equals("xssAttack")) {
			address = "WEB-INF/pages/Xss.jsp";
		}
		else if(action.equals("sessionHijacking")) {
			address = "WEB-INF/pages/SessionHijacking.jsp";
		}
		else if(action.equals("logout")) {
			SessionController.getSessionController().terminateSession(request, 0);
			response.sendRedirect("./LoginServlet");
			return;
		}
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
		
	}
		

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response); 
	}

}
