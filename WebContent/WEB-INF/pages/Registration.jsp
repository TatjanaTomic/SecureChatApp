<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registracija</title>
<script type="text/javascript" src="reg_script.js"></script>
</head>
<body>
	<form name="form" method="POST" action="./RegistrationServlet?action=signup">
		<h1>SecureChatApp - Registracija</h1>
		<hr />
		<input autofocus="autofocus" type="text" required="required" name="firstName" id="firstName" placeholder="Ime" maxlength=45 size=25 />
		<br />
		<br />
		<input autofocus="autofocus" type="text" required="required" name="lastName" placeholder="Prezime" maxlength=45 size=25 />
		<br />
		<br />
		<input autofocus="autofocus" type="email" required="required" name="email" placeholder="Email" maxlength=60 size=25/>
		<br />
		<br />
		<input autofocus="autofocus" type="text" required="required" name="username" placeholder="Korisničko ime" maxlength=20 size=25/>
		<br />
		<br />
		<input autofocus="autofocus" type="password" required="required" name="password" placeholder="Lozinka" maxlength=50 size=25/>
		<br />
		<br />
		<input autofocus="autofocus" type="password" required="required" name="password2" placeholder="Ponovljena lozinka" maxlength=50 size=25/>
		<br />
		<br />
		<input type="submit" name="submit" value="Registrujte se" class="button">
		<h3><%=session.getAttribute("notification")!=null?session.getAttribute("notification").toString():""%></h3>
		<hr />
		<a href="./LoginServlet">Prijava na sistem &gt;&gt;</a>
		
	</form>
</body>
</html>