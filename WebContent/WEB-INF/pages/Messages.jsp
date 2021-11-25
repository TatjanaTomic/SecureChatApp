<%@ page import="etf.sni.beans.ConversationBean" %>
<%@ page import="etf.sni.dto.Message" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:useBean id="conversation" class="etf.sni.beans.ConversationBean" scope="request"></jsp:useBean>

<!DOCTYPE html>
<%
	if(session.getAttribute("user") == null ) {
		response.sendRedirect("./LoginServlet?action=showLogin");
	}
	if(session.getAttribute("messages") != null) {
		conversation = (ConversationBean) session.getAttribute("messages");
	}
%>
<html>
<head>
<meta charset="UTF-8">
<title>Poruke</title>
</head>
<body>
	<h1>Poruke</h1>

	<%
		String user1 = conversation.getUser1();
		String user2 = conversation.getUser2();
		
		if(user1 != null && user2 != null) {
			out.println("<h5>Trenutni korisnik: " + user1 + "</h5>");
			out.println("<h5>Sagovornik: " + user2 + "</h5>");
		}
	
		if(conversation.getMessages() != null) {
			for(Message m:conversation.getMessages()) {
				out.print("<p><b>" + m.getContent() + "</b><br/>Posiljalac: " + m.getSender() + "; Primalac: " + m.getReceiver() + "; Datum: " + m.getDateTime() + "</p>");
			}
		}
	%>

	<form name="newMessageForm" method="POST" action="./ChatServlet?action=sendMessage">
		<textarea rows="5" cols="50" name="newMessage" id=newMessage required="required"></textarea>
		<br />
		<input type="submit" name="submit" value="Pošalji poruku" class="button">
	</form>
	
	<hr />
	<a href="./DetectionServlet?action=logout">Odjavite se &gt;&gt;</a>

</body>
</html>