<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta charset="UTF-8">
<title>회원탈퇴</title>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<style>
body {
	font-family: Arial, sans-serif;
	margin: 0;
	padding: 0;
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100vh;
	background-color: #ffffff;
}

.container {
	background: white;
	padding: 20px;
	border-radius: 8px;
	width: 400px;
}

h3 {
	text-align: center;
	margin-bottom: 20px;
}

.form-group {
	margin-bottom: 15px;
}

label {
	display: block;
	margin-bottom: 5px;
	font-weight: bold;
}

input[type="text"], input[type="password"], input[type="email"] {
	width: 100%;
	padding: 10px;
	border: 1px solid #ccc;
	border-radius: 4px;
	box-sizing: border-box;
}

button {
	background-color: #ff7f50;
	color: white;
	border: none;
	border-radius: 4px;
	cursor: pointer;
	padding: 10px;
	margin-top: 10px;
	width: 100%;
}

button:hover {
	background-color: #ff6347;
}

.result {
	margin-left: 10px; /* 버튼과 결과 사이의 간격 */
	font-weight: bold;
	display: inline-block; /* 수평 정렬 */
}
</style>
</head>
<body>
	<div class="container">
		<h3>회원탈퇴</h3>
		<form id="withdrawalForm" action="memberDelete" method="post">
			<div class="form-group">
				<label for="id">아이디</label> 
				<input type="text" name="id" required placeholder="아이디" />
			</div>
			<div class="form-group">
				<label for="pw">비밀번호</label> 
				<input type="password" name="pw" required placeholder="비밀번호" />
			</div>
			<div class="form-group">
				<label for="email">이메일</label> 
				<input type="email" name="email" required placeholder="이메일" />
			</div>
			<div class="submit-group">
				<button type="submit">탈퇴하기</button>
			</div>
		</form>
	</div>

	<script>
	</script>
</body>
</html>