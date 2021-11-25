<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Sertifikat</title>
</head>
<body>
	<h1>Molimo Vas da priložite korisnički sertifikat</h1>
	
	<form action="./LoginServlet?action=uploadCert" method="POST" enctype="multipart/form-data">
		<input type="file" name="file">
		<br />
		<br />
		<input type="submit" value="Posalji sertifikat">
	</form>
	
</body>
</html>