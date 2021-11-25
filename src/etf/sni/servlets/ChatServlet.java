package etf.sni.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import etf.sni.beans.ConversationBean;
import etf.sni.beans.UsersBean;
import etf.sni.controller.InputController;
import etf.sni.controller.MessagesController;
import etf.sni.controller.SessionController;
import etf.sni.dto.User;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private User currentUser;
	public static UsersBean onlineUsers;
    
    public ChatServlet() {
        super();
        onlineUsers = new UsersBean();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		if(SessionController.getSessionController().isSessionHijacking(request)) {
			SessionController.getSessionController().terminateSession(request, 3);
			response.sendRedirect("./DetectionServlet?action=sessionHijacking");
			return;
		}
		
		String action = request.getParameter("action");
		
		HttpSession session = request.getSession();
		currentUser = (User) session.getAttribute("user");
		
		if (action == null || currentUser == null) {
			response.sendRedirect("./LoginServlet");
		}
		else if (action.equals("showMessages")) {
			String selectedUser = request.getParameter("selectedUser");
			String address = "WEB-INF/pages/OnlineUsers.jsp";
			
			if(selectedUser != null) {
				ConversationBean conversation = new ConversationBean(currentUser.getUsername(), selectedUser);
				session.setAttribute("messages", conversation);
				address = "WEB-INF/pages/Messages.jsp";
			}
		
			RequestDispatcher dispatcher = request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}
		else if (action.equals("showOnlineUsers")) {
			
			onlineUsers.loginUser(currentUser);
			session.setAttribute("users", onlineUsers);
			
			String address = "WEB-INF/pages/OnlineUsers.jsp";
			RequestDispatcher dispatcher = request.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}
		else {
			
			//TODO: ispravi/dovrsi ovo
			
			onlineUsers.logoutUser(currentUser);
			session.removeAttribute("user");
			session.removeAttribute("users");
			
			session.invalidate();
			
			response.sendRedirect("./DetectionServlet?action=pageNotFound");
		}
		
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
		
		if(action != null && action.equals("sendMessage")) {
			String newMessage = request.getParameter("newMessage");
			
			if(newMessage != null) {
				
				int value = InputController.getInputController().checkInput(Arrays.asList(newMessage));
				if (value == 1) {
					response.sendRedirect("./DetectionServlet?action=sqlInjectionAttack");
					SessionController.getSessionController().terminateSession(request, 1);
					//TODO: Treba terminirati i sesiju sagovornika
					return;
				}
				else if (value == 2) {
					response.sendRedirect("./DetectionServlet?action=xssAttack");
					SessionController.getSessionController().terminateSession(request, 2);
					//TODO: Treba terminirati i sesiju sagovornika
					return;
				}
				else {
					ConversationBean conversation = (ConversationBean) session.getAttribute("messages");
					String sender = conversation.getUser1();
					String receiver = conversation.getUser2();
					
					MessagesController.getMessagesController().saveNewMessage(newMessage, sender, receiver);
					
					conversation = new ConversationBean(sender, receiver);
					session.setAttribute("messages", conversation);
					
					response.sendRedirect("./ChatServlet?action=showMessages&selectedUser=" + receiver);
					
				}
				
			}
		}
	}

}
