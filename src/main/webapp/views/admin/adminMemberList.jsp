<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta charset="UTF-8">
<title>이런저런</title>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/css/bootstrap.min.css">
<link rel="stylesheet" href="/resources/css/common.css">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.2/js/bootstrap.min.js"></script>
<script src="/resources/js/jquery.twbsPagination.js" type="text/javascript"></script>
<style>
.input-container {
    display: flex;        /* Flexbox 사용 */
    align-items: center; /* 수직 중앙 정렬 */
    margin-bottom: 20px;
    margin-top: 20px;
	}
	#searchForm{
	margin-top: 20px; 
	margin-bottom: 10px; 
	}
	
	#text{
   
    margin-right: 15px
	}
	body {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    margin: 0;
	}
	/* 콘텐츠와 사이드바 감싸는 래퍼 */
	.content-wrapper {
	    display: flex;
	    width: 100%;
	    margin: 80px 10px; /* 헤더 높이만큼 여백 */
	    flex-grow: 1; /* 남은 공간 채우기 */
	}

	/* 사이드바 스타일 */
	.fixed-left {
	    width: 300px;
	    border-right: 1px solid #ccc;
	    padding: 20px;
	    position: sticky;
	    top: 80px; /* 헤더 아래에 고정 */
	    height: calc(100vh - 80px); /* 화면 높이에 맞추기 */
	    overflow-y: auto;
	}
	.fixed-left p{
	    margin: 15px 0;
	    line-height: 1.5;
	    font-size: 20px;
	}
	
	#admin_name{
	font-weight: 800;
	font-size: 23px;
	}
	
	.image img {
	    width: 35%;  /* 또는 원하는 픽셀 값 */
	    height: auto;
	    margin-bottom: 20px; /* 비율을 유지 */
		}
	/* 메인 콘텐츠 */
	.main-content {
	    flex: 1; /* 남은 공간 채우기 */
	    padding: 20px 100px 20px 40px;
	    overflow: auto;
	}
	#content{
    	width: 750px; /* 너비를 250픽셀로 설정 */
        height: 350px;
        resize: vertical; /* 높이를 50픽셀로 설정 */
        font-size: 20px; /* 글자 크기를 18픽셀로 설정 */
        padding: 10px; /* 내부 여백을 10픽셀로 설정 */
    
    }
	.btn-area {
		text-align: right;
	}
	.btn01-l{
	margin-top: 10px;
	margin-bottom: 10px;
	}
	

</style>
</head>
<body>
    <!-- 헤더 -->
    <jsp:include page="../header.jsp"/>

    <!-- 콘텐츠 영역 (사이드바와 메인 콘텐츠를 감싸는 래퍼) -->
    <div class="content-wrapper">
        <!-- 사이드바 -->
        <aside class="fixed-left">
            <div class="image">
                <img class="profile-img" src="/resources/img/common/admin_profile.png" alt="관리자 프로필 이미지"/>
            </div>
            <p class="title2" id="admin_name">관리자</p>
            <p class="title3" style="cursor: pointer;" onclick="location.href='/adminMember'">회원정보</p>
            <p class="title3" style="cursor: pointer;" onclick="location.href='/adminReport'">신고</p>
            <p class="title3" style="cursor: pointer;" onclick="location.href='/adminAsk'">문의하기</p>
            <p class="title3" style="cursor: pointer;" onclick="location.href='/adminTag'">태그</p>
            <p class="title3" style="cursor: pointer;" onclick="location.href='/adminIconListView'">아이콘</p>
            <p class="title3" style="cursor: pointer;" onclick="location.href='/adminPopup'">팝업</p>
            <p class="title3" style="cursor: pointer;" onclick="location.href='/adminCode'">구분코드</p>
            <p class="title3" style="cursor: pointer;" onclick="location.href='/adminJoin'">회원가입</p>
        </aside>

        <!-- 메인 콘텐츠 -->
        <main class="main-content">
            <p class="title1">회원정보</p>
            <div class="btn02-l" onclick="location.href='/adminMember'">일반회원</div>
            <div class="btn03-l" onclick="location.href='/admin'">관리자</div>

            <form id="searchForm" onsubmit="return false;">
                <select id="searchOption">
                    <option value="id">아이디</option>
                    <option value="nickname">닉네임</option>
                    <option value="email">이메일</option>
                </select>
                <input class="input-txt-l" type="text" id="searchKeyword" placeholder="검색어를 입력하세요"/>
                <input class="btn-sch" type="button" onclick="pageCall(1)" value="검색"/>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>아이디</th>
                        <th>닉네임</th>
                        <th>이메일</th>
                        <th>권한</th>
                        <th onclick="sortby('report_count')"  style="cursor: pointer;">신고누적수</th>
                        <th onclick="sortby('join_date')"  style="cursor: pointer;">가입일시</th>
                    </tr>
                </thead>
                <tbody id="list"></tbody>
                <tr>
                    <th colspan="6">
                        <div class="container">
                            <nav aria-label="Page navigation">
                                <ul class="pagination" id="pagination"></ul>
                            </nav>
                        </div>
                    </th>
                </tr>
            </table>
        </main>
    </div>

    <!-- 푸터 -->
    <jsp:include page="../footer.jsp"/>
</body>
<script>
var msg = '${msg}';
if (msg != '') {
   alert(msg);
}

var show = 1;
var currentSortField = '';
var currentSortOrder = 'ASC';
var paginationInitialized = false;  // 페이지네이션 초기화 여부 확인 변수

$(document).ready(function() {
    pageCall(show); // 초기 페이지 로드
});

function pageCall(page, sortField = '', sortOrder = '') {
    var keyword = $('#searchKeyword').val();
    var opt = $('#searchOption').val();

    $.ajax({
        type: 'Get',
        url: '/adminMemberList',
        data: {
            page: page,
            cnt: 15,
            opt: opt,
            keyword: keyword,
            sortField: sortField,
            sortOrder: sortOrder
        },
        datatype: 'JSON',
        success: function(data) {
            console.log(data);
            drawList(data.list);

            // 페이지네이션 초기화 및 재설정
            if (paginationInitialized) {
                $('#pagination').twbsPagination('destroy'); // 기존 페이지네이션 제거
            }

            if (data.totalPages > 0) { // 검색 결과가 있을 때만 페이지네이션 생성
                $('#pagination').twbsPagination({
                    totalPages: data.totalPages,
                    visiblePages: 10,
                    startPage: page,
                    initiateStartPageClick: false, // 페이지네이션 초기 클릭 방지
                    onPageClick: function(evt, page) {
                        pageCall(page, sortField, sortOrder);
                    }
                });
                paginationInitialized = true; // 페이지네이션 초기화 완료 상태로 설정
            } else {
                paginationInitialized = false; // 검색 결과가 없으면 초기화 상태로 설정
            }
        },
        error: function(e) {
            console.log(e);
        }
    });
}

function sortby(field) {
    if (currentSortField == field) {
        currentSortOrder = currentSortOrder == 'ASC' ? 'DESC' : 'ASC';
    } else {
        currentSortField = field;
        currentSortOrder = 'ASC';
    }

    $('#pagination').twbsPagination('destroy'); // 기존 페이지네이션 초기화
    paginationInitialized = false;
    pageCall(1, currentSortField, currentSortOrder);
}
function drawList(list) {
    var content = '';
    for (var view of list) {
        content += '<tr>';
        content += '<td style="' + (view.report_status == 'Y' ? 'color: blue;' : '') + '">' + view.id + '</td>';
        content += '<td><a href="/adminMemberDetail/' + view.id + '">' + view.nickname + '</a></td>';
        content += '<td>' + view.email + '</td>';
        content += '<td><a href="/memberRight/' + view.nickname + '" style="color: orange;"  id="locationAdmin">권한</a></td>';
        content += '<td>' + view.report_count + '</td>';
        content += '<td>' + view.join_date + '</td>';
        content += '</tr>';
    }
    $('#list').html(content);
}


</script>
<script src="/resources/js/common.js" type="text/javascript"></script>
<script src="/resources/js/layerPopup.js"></script>
</html>