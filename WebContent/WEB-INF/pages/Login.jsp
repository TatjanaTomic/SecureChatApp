<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Prijava na sistem</title>
</head>
<body>
	<form method="POST" action="./LoginServlet?action=login">
		<h1>SecureChatApp - Prijava na sistem</h1>
		<hr />
		<input autofocus="autofocus" type="text" required="required" name="username" placeholder="Korisničko ime" />
		<br />
		<br />
		<input autofocus="autofocus" type="password" required="required" name="password" placeholder="Lozinka" />
		<br />
		<br />
		<input type="submit" name="submit" value="Login" class="button">
		<hr />
		<h3><%=session.getAttribute("notification")!=null?session.getAttribute("notification").toString():""%></h3>
		<br />
		<a href="./RegistrationServlet">Registrujte se &gt;&gt;</a>
	</form>
</body>
</html>