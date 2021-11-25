<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Token</title>
</head>
<body>
	<h1>Poslali smo Vam token na mail. Molimo Vas da unesete token.</h1>
	<form name="form" method="POST" action="./LoginServlet?action=checkToken">
		<input autofocus="autofocus" type="text" required="required" name="token" placeholder="Token" maxlength=6 size=25 />
		<br />
		<br />
		<h3><%=session.getAttribute("notification")!=null?session.getAttribute("notification").toString():""%></h3>
		<br />
		<input type="submit" name="submit" value="Posalji token" class="button">
	</form>
</body>
</html>