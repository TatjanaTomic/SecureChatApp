<%@ page import="etf.sni.dto.User" %>
<%@ page import="etf.sni.beans.UsersBean" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<jsp:useBean id="users" class="etf.sni.beans.UsersBean" scope="application"></jsp:useBean>
    
<!DOCTYPE html>
<%
	if(session.getAttribute("user") == null ) {
		response.sendRedirect("./LoginServlet?action=showLogin");
	}
	if(session.getAttribute("users") != null) {
		users = (UsersBean) session.getAttribute("users");
	}
%>
<html>
<head>
<meta charset="UTF-8">
<title>Korisnici</title>
</head>
<body>
	<h1>Prijavljeni korisnici</h1>

	<%
		User currentUser = (User) session.getAttribute("user");
		if(currentUser != null) {
			for(User u:users.getUsers()) {
				if(!u.getUsername().equals(currentUser.getUsername()))
					out.print("<a href=\"./ChatServlet?action=showMessages&selectedUser=" + u.getUsername() + "\">" + u.getUsername() + "</a><br />");
			}
		}
	%>
	
	<hr />
	<a href="./DetectionServlet?action=logout">Odjavite se &gt;&gt;</a>
</body>
</html>