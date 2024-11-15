 package com.erunjrun.crew.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.erunjrun.crew.dto.CrewDTO;
import com.erunjrun.crew.dto.CrewMemberDTO;
import com.erunjrun.crew.dto.CrewNoticeDTO;
import com.erunjrun.crew.service.CrewService;
import com.erunjrun.image.dto.ImageDTO;
import com.erunjrun.main.controller.AlarmController;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/crew")
@RequiredArgsConstructor
public class CrewController {
    
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired CrewService crew_service;
    @Autowired AlarmController alarm_controller;

    
	@PostMapping(value="/image-upload")
	public ResponseEntity<?> imageUpload(@RequestParam("file") MultipartFile file){
		
		logger.info("file : " + file.getOriginalFilename());
		
		
		try {
			Map<String, Object> resultFileMap = crew_service.saveFile(file);
			return ResponseEntity.ok(resultFileMap);
			
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
		}
	}
	
	@PostMapping(value="/write")
	public Map<String, Object> submitPost(@RequestParam("crew_img") MultipartFile crew_img, 
			@ModelAttribute CrewDTO crewDto, @RequestParam("imgsJson") String imgsJson) { // Dto랑 이름이 같으면 착각하고 에러나서 이름 다르게!
		
		Map<String, Object> resultMap = new HashMap<>();
		
		logger.info("ori_name =>" + crew_img.getOriginalFilename());
		
		// JSON -> List<FileDto> 변환
		ObjectMapper objectMapper = new ObjectMapper();
		List<ImageDTO> imgs = null;
		try {
			// TypeFactory를 사용하여 제네릭 타입을 처리
	        imgs = objectMapper.readValue(imgsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, ImageDTO.class));
	        crewDto.setImgs(imgs);  // 변환한 리스트를 DTO에 설정
		} catch (Exception e) {
			logger.error("파싱 오류 : {}", e.getMessage());
			return Map.of("error", e.getMessage());
		}
		
		if (imgs != null && !imgs.isEmpty()) {
		    for (ImageDTO img : imgs) {
		        logger.info("Original Filename: " + img.getImg_ori());
		        logger.info("New Filename: " + img.getImg_new());
		    }
		}
		
		logger.info("DTO : " + crewDto.toString());

		if(crew_service.sumbitPost(crewDto, crew_img)) {
			// 저장 완료 후 응답 반환
			// return ResponseEntity.ok("글 저장 성공");
			
			logger.info("글 업로드 완료");
			
			resultMap.put("success", true);
		}

		return resultMap;
	}

	@DeleteMapping(value="/delete")
	public Map<String, Object> crewDelete(int crew_idx){
		
		logger.info("cres_idx => "+crew_idx);
		
		Map<String, Object> resultMap = new HashMap<>();
		
		if(crew_service.crewDelete(crew_idx)) {
			resultMap.put("success", true);
		}else {
			resultMap.put("success", false);
		}
		
		return resultMap;
	}
	
	@PutMapping(value="/update")
	public Map<String, Object> crewUpdate(@RequestParam("crew_img") MultipartFile crew_img, 
			@ModelAttribute CrewDTO crewDto, @RequestParam("imgsJson") String imgsJson){
		
		Map<String, Object> resultMap = new HashMap<>();
		
		logger.info("ori_name =>" + crew_img.getOriginalFilename());
		
		ObjectMapper objectMapper = new ObjectMapper();
		List<ImageDTO> imgs = null;
		try {
	        imgs = objectMapper.readValue(imgsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, ImageDTO.class));
	        crewDto.setImgs(imgs);  
		} catch (Exception e) {
			logger.error("파싱 오류 : {}", e.getMessage());
			return Map.of("error", e.getMessage());
		}
		
		if (imgs != null && !imgs.isEmpty()) {
		    for (ImageDTO img : imgs) {
		        logger.info("Original Filename: " + img.getImg_ori());
		        logger.info("New Filename: " + img.getImg_new());
		    }
		}
		
		logger.info("수정하는 DTO : " + crewDto.toString());

		if(crew_service.crewUpdate(crewDto, crew_img)) {
			logger.info("글 업로드 완료");
			
			String page = "crewUpdateView";
			
			resultMap.put("success", true);
			resultMap.put("page", page);
			
		}

		return resultMap;
		
	}
	
	@PostMapping(value="/detail")
	public Map<String, Object> crewDetail(String crew_idx) {
		
		logger.info("idx => " + crew_idx);
		
		Map<String, Object> resultMap = new HashMap<>();
		
		int crew_idxs = Integer.parseInt(crew_idx); 
		
		CrewDTO crewDto = crew_service.crewDetail(crew_idxs);
		
		resultMap.put("result", crewDto);
		resultMap.put("success", true);
		
		
		return resultMap;
	}
	
	@PostMapping(value="/memberList")
	public Map<String, Object> crewMemberList(String crew_idx){
		logger.info("member crew_idx =>" + crew_idx);
		
		Map<String, Object> resultMap = new HashMap<>();
		
		int crew_idxs = Integer.parseInt(crew_idx); 
		
		List<CrewMemberDTO> crewMemberList = crew_service.crewMemberList(crew_idxs);
		
		int page = 1;
		int cnt = 15;
		String keyword = "";
		
		List<CrewMemberDTO> crewApplicationList = crew_service.crewApplicationList(crew_idxs, page, cnt, keyword);
		
		resultMap.put("result", crewMemberList);
		resultMap.put("application", crewApplicationList);
		resultMap.put("success", true);
		
		return resultMap;
		
	}
	
	@PostMapping(value="/applicationWrite")
	public Map<String, Object> crewApplicationWrite(String loginId, String crew_idx, String code_name){
		
		logger.info("loginId =>" + loginId);
		logger.info("crew_idx =>" + crew_idx);
		logger.info("code_name =>" + code_name);

		Map<String, Object> resultMap = new HashMap<>();
		int crew_idxs = Integer.parseInt(crew_idx);
		
		Map<String, Object> parmeterMap = new HashMap<>();
		parmeterMap.put("id", loginId);
		parmeterMap.put("crew_idx", crew_idxs);
		parmeterMap.put("code_name", code_name);

		boolean success = false;
		String msg = "";
		
		// 크루 신청
		if(code_name.equals("C100")) {
			success = crew_service.crewApplicationWrite(parmeterMap);
			msg = "크루 신청";
			logger.info("크루신청");
		// 크루 신청 취소
		}else if(code_name.equals("C104")) {
			success = crew_service.crewApplicationCancel(parmeterMap);
			msg = "신청 취소";
			logger.info("크루신청 취소");
		// 크루 탈퇴
		}else if(code_name.equals("C105")) {
			success = crew_service.crewMemberCencel(parmeterMap);
			msg = "크루 탈퇴";
			logger.info("크루탈퇴");
			
		// 크루 승인
		}else if(code_name.equals("C101")) {
			logger.info("크루승인");
			success = crew_service.crewMemberApproval(parmeterMap);
			msg = "크루 승인";
		// 크루 미승인
		}else if(code_name.equals("C102")) {
			logger.info("크루 미승인");
			success = crew_service.crewMemberRefusal(parmeterMap);
			msg = "크루 미승인";
		// 크루 퇴출
		}else{
			logger.info("크루퇴출");
		}
		
		resultMap.put("success", success);
		resultMap.put("msg", msg);
		
		return resultMap;
	}
	
	@PostMapping(value="/likeIs")
	public Map<String, Object> likeIs(String loginId, String crew_idx){
		
		Map<String, Object> resultMap = new HashMap<>();
		
		logger.info("crew_idx =>"+crew_idx);
		logger.info("loginId =>"+loginId);
		
		if(loginId != null && loginId != "") {
			int crew_idxs = Integer.parseInt(crew_idx);
			
			Map<String, Object> parmeterMap = new HashMap<>();
			
			parmeterMap.put("id", loginId);
			parmeterMap.put("crew_idx", crew_idxs);
			
			int count = crew_service.likeIs(parmeterMap);
			
			if(count > 0) {
				resultMap.put("like", count);
				resultMap.put("success", true);
			}
			
			
			return resultMap;
		}else{
			resultMap.put("seccess", false);
		}
		
		
		return resultMap;
	}
	
	@PostMapping(value="/likeRequest")
	public Map<String, Object> likeRequest(String loginId, String crew_idx, String likeCrew){
		Map<String, Object> resultMap = new HashMap<>();
		
		logger.info("crew_idx =>"+crew_idx);
		logger.info("loginId =>"+loginId);
		logger.info("likeCrew =>"+likeCrew);

		String msg = "";
		boolean success = false;
		
		int crew_idxs = Integer.parseInt(crew_idx);
		
		Map<String, Object> parmeterMap = new HashMap<>();
		
		parmeterMap.put("id", loginId);
		parmeterMap.put("crew_idx", crew_idxs);
		
		if(likeCrew.equals("N")) {
			success = crew_service.likeRequest(parmeterMap);
			msg = "좋아요 등록 성공";
			
		}else {
			success = crew_service.likeCencel(parmeterMap);
			msg = "좋아요 취소 성공";
		}
		
		if(success) {
		}else {
			msg="좋아요 실패";
		}
		
		resultMap.put("success", success);
		resultMap.put("msg", msg);
		
		return resultMap;
	}
	
	@GetMapping(value="/list")
	public Map<String, Object> crewList(@RequestParam(value = "filtering", required = false) List<String> filtering,
	        @RequestParam(value = "page", defaultValue = "1") int page,
	        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
		
	    if (filtering == null) {
	        logger.info("필터 없음");
	    }else {
	    	logger.info("필터 => " +filtering);
	    	
	    }
	    
	    logger.info("page : pageSize" + page + ":" + pageSize);
	    
		Map<String, Object> resultMap = new HashMap<>();
		List<Map<String, Object>> crewList = crew_service.crewList(filtering, page, pageSize);
	    resultMap.put("result", crewList);
		
		return resultMap;
	}
	
	@PostMapping(value="/applicationMemberList")
	public Map<String, Object> ApplicationMemberList(
			@RequestParam String crew_idx,
			@RequestParam(value = "page", defaultValue = "1") int page, 
			@RequestParam(value = "cnt", defaultValue = "15") int cnt,
			@RequestParam(defaultValue = "", value="keyword") String keyword){
		
		logger.info("crew_idx => " + crew_idx);
		logger.info("page : cnt => " + page + " : " + cnt);
		logger.info("keyword=>" + keyword);
		
		int crew_idxs = Integer.parseInt(crew_idx);
		
		Map<String, Object> resultMap = new HashMap<>();
		
		resultMap.put("result", crew_service.crewApplicationList(crew_idxs ,page, cnt, keyword));
		
		return resultMap;
	}
	
	@PostMapping(value="/applicationAdminList")
	public Map<String, Object> applicationAdminList(
			@RequestParam String crew_idx,
			@RequestParam(value = "page", defaultValue = "1") int page, 
			@RequestParam(value = "cnt", defaultValue = "15") int cnt,
			@RequestParam(defaultValue = "", value="keyword") String keyword){
		
		logger.info("crew_idx => " + crew_idx);
		logger.info("page : cnt => " + page + " : " + cnt);
		logger.info("keyword=>" + keyword);
		
		int crew_idxs = Integer.parseInt(crew_idx);
		
		Map<String, Object> resultMap = new HashMap<>();
		
		resultMap.put("result", crew_service.applicationAdminList(crew_idxs ,page, cnt, keyword));
		
		return resultMap;
	}
	
	@PostMapping(value="/AdminUpdate")
	public Map<String, Object> crewAdminUpdate(@RequestParam(value="crew_idx") String crew_idx, @RequestParam(value="id") String id, @RequestParam(value="leader") String leader){
		Map<String, Object> resultMap = new HashMap<>();
		
		logger.info("권한 양도 id =>" + id);
		logger.info("리더 id =>" + leader);
		
		int crew_idxs = Integer.parseInt(crew_idx);
		
		resultMap.put("success", crew_service.crewAdminUpdate(id, leader, crew_idxs));
		
		alarm_controller.crewAdmin(crew_idxs, id, leader);
		
		return resultMap;
	}
	
	@PostMapping(value="/crewExpel")
	public Map<String, Object> crewExpel(@RequestParam(value="crew_idx") String crew_idx, @RequestParam(value="ids") List<String> ids){
		Map<String, Object> resultMap = new HashMap<>();
		
		logger.info(" 퇴출 id =>" + ids);
		
	    Map<String, Object> params = new HashMap<>();
	    int crew_idxs = Integer.parseInt(crew_idx);
	    params.put("crew_idx", crew_idxs);
	    params.put("ids", ids);
	    params.put("code_name", "C103");
	    
	    // 서비스에 Map 전달
	    resultMap.put("success", crew_service.crewExpel(params));
	    
	    alarm_controller.crewMemberExpel(crew_idxs, ids);
	    
	    return resultMap;
	}
	
	@PostMapping(value="/priorityOverlay")
	public Map<String, Object> crewPriorityOverlay(@RequestParam(value="crew_idx") String crew_idx, 
			@RequestParam(value="priority") String priority){
		
		logger.info("crew_idx : priority " + crew_idx + " : " + priority);
		
		
		CrewNoticeDTO crewNoticeDto = new CrewNoticeDTO();
		crewNoticeDto.setCrew_idx(Integer.parseInt(crew_idx));
		crewNoticeDto.setPriority(priority);
		
		Map<String, Object> resultMap = crew_service.crewPriorityOverlay(crewNoticeDto);
		return resultMap;
	}
	
	@PostMapping(value="/noticeWrite")
	public Map<String, Object> crewNoticeWrite(@ModelAttribute CrewNoticeDTO crewNoticeDto, @RequestParam("imgsJson") String imgsJson){
		Map<String, Object> resultMap = new HashMap<>();
		
		ObjectMapper objectMapper = new ObjectMapper();
		List<ImageDTO> imgs = null;
		try {
			// TypeFactory를 사용하여 제네릭 타입을 처리
	        imgs = objectMapper.readValue(imgsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, ImageDTO.class));
	        crewNoticeDto.setImgs(imgs);  // 변환한 리스트를 DTO에 설정
		} catch (Exception e) {
			logger.error("파싱 오류 : {}", e.getMessage());
			return Map.of("error", e.getMessage());
		}
		
		if (imgs != null && !imgs.isEmpty()) {
		    for (ImageDTO img : imgs) {
		        logger.info("Original Filename: " + img.getImg_ori());
		        logger.info("New Filename: " + img.getImg_new());
		    }
		}
		
		logger.info("crewNoticeDto => " + crewNoticeDto.toString());
		
		if(crew_service.crewNoticeWrite(crewNoticeDto)) {
			logger.info("공지사항 업로드 완료");
			resultMap.put("success", true);
			resultMap.put("notice_idx", crewNoticeDto.getNotice_idx());
		}
		
		
		return resultMap;
	}
	
	
	@PutMapping(value="/noticePriorityUpdate")
	public Map<String, Object> crewNoticePriorityUpdate(@RequestParam(value="crew_idx") String crew_idx, 
			@RequestParam(value="priority") String priority){
		
		logger.info("crew_idx" + crew_idx);
		logger.info("priority =>" + priority);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("success", crew_service.crewNoticePriorityUpdate(crew_idx, priority));
		return resultMap;
		
	}
	
	@PostMapping(value="/noticeList")
	public Map<String, Object> crewNoticeList(@RequestParam String crew_idx,
			@RequestParam(value = "page") int page, 
			@RequestParam(value = "cnt") int cnt,
			@RequestParam(defaultValue = "", value = "option") String option,
			@RequestParam(defaultValue = "", value="keyword") String keyword){
		
		logger.info("keyword => "+keyword);
		logger.info("option => "+option);
		logger.info("crew_idx => "+crew_idx);
		logger.info("page => "+page);
		logger.info("cnt => "+cnt);
		
		int crew_idxs = Integer.parseInt(crew_idx);
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("result", crew_service.crewNoticeList(crew_idxs, page, cnt, option, keyword));
		
		return resultMap;
		
	}
	
	@PostMapping(value="/sendNoticeUpdate")
	public Map<String, Object> crewNoticeUpdate(@ModelAttribute CrewNoticeDTO crewNoticeDto, @RequestParam("imgsJson") String imgsJson){
		Map<String, Object> resultMap = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<ImageDTO> imgs = null;
		
		String sun = crewNoticeDto.getPriority();
		logger.info("순위 ====> " + sun); // 순위는 잘 받아옴!
		
		try {
			// TypeFactory를 사용하여 제네릭 타입을 처리
	        imgs = objectMapper.readValue(imgsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, ImageDTO.class));
	        crewNoticeDto.setImgs(imgs);  // 변환한 리스트를 DTO에 설정
		} catch (Exception e) {
			logger.error("파싱 오류 : {}", e.getMessage());
			return Map.of("error", e.getMessage());
		}
		
		if (imgs != null && !imgs.isEmpty()) {
		    for (ImageDTO img : imgs) {
		        logger.info("Original Filename: " + img.getImg_ori());
		        logger.info("New Filename: " + img.getImg_new());
		    }
		}
		
		logger.info("crewNoticeDto => " + crewNoticeDto.toString());
		
		if(crew_service.crewNoticeUpdate(crewNoticeDto)) {
			logger.info("공지사항 업로드 완료");
			resultMap.put("success", true);
		}
		
		return resultMap;
	}
	
	@PostMapping(value="/noticeDetail")
	public Map<String, Object> crewNoticeDetail(@RequestParam int notice_idx,HttpSession session){
		
		String loginId = (String) session.getAttribute("loginId");
		/*
		 * MemberDTO nickname = null; if(loginId != null) { nickname =
		 * crew_service.nickName(loginId); logger.info("닉네임 맞냐 :"+nickname.toString());
		 * }
		 */
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("result", crew_service.crewNoticeDetail(notice_idx));
//		resultMap.put("nickname", nickname);
		
		return resultMap;
	}
	
	@DeleteMapping(value="/noticeDelete")
	public Map<String, Object> crewNoticeDelete(@RequestParam int notice_idx){
		
		logger.info("notice_idx => " + notice_idx);
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("success", crew_service.crewNoticeDelete(notice_idx));
		
		return resultMap;
	}
	

	@PostMapping(value="/updateView")
	public Map<String, Object> crewUpdateView(@RequestParam int crew_idx){
		logger.info("crew_idx... =>" + crew_idx);
		try {
			logger.info("crew_idx?? =>" + crew_idx);
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("result", crew_service.crewUpdateView(crew_idx));
			return resultMap;
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	@PostMapping(value="/leaderCheck")
	public Map<String, Object> crewLeaderCheck(@RequestParam int crew_idx){
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("leaderId", crew_service.crewLeaderCheck(crew_idx));
		
		return resultMap;
	}
	
	@PostMapping(value="/likeCrew")
	public Map<String, Object> crewLikeCrew(@RequestParam String id){
		try {
			Map<String, Object> resultMap = new HashMap<>();
			
			resultMap.put("result", crew_service.crewLikeCrew(id));
			
			return resultMap;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping(value="/adminMemberUpdate")
	public boolean crewMemberAdminUpdate(@RequestParam String result, @RequestParam int crew_idx, @RequestParam String id, @RequestParam String leader) {
		boolean success = false;
		Map<String, Object> param = new HashMap<>();
		if(result.equals("Y")) {
			param.put("crew_idx", crew_idx);
			param.put("id", id);
			logger.info("id => " + id);
			logger.info("leader => " + leader);
			param.put("is_agree", result);
			param.put("leader", leader);
		}else {
			param.put("crew_idx", crew_idx);
			param.put("id", id);
			param.put("is_agree", result);
		}
		
		if(crew_service.crewMemberAdminUpdate(param) >0) {
			success = true;
		}
		return success;
		
	}
	
	@GetMapping(value="/adminResultCheck")
	public boolean crewAdminResultCheck(@RequestParam int crew_idx, @RequestParam String id) {
		
		logger.info("크루 권한 있는지 체크하는 메서드 실행!!!!!!!!!!!!!");
		boolean success = false;
		try {
			Map<String, Object> param = new HashMap<>();
			param.put("crew_idx", crew_idx);
			param.put("id", id); // 크루장 id
			if(crew_service.crewAdminResultCheck(param) == 0) { // 0일 경우 아직 아무도 승인 안한 상태
				return true;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return success;
	}
	
	@GetMapping(value="/adminOverlay")
	public boolean crewAdminOverlay(@RequestParam String id, @RequestParam int crew_idx) {
		
		try {
			Map<String, Object> param = new HashMap<>();
			param.put("crew_idx", crew_idx);
			param.put("id", id);
			if(crew_service.crewAdminOverlay(param) == 0) {
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@GetMapping(value="/tagResult")
	public Map<String, Object> crewTagResult(){
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("tag", crew_service.crewTagResult());
		
		return resultMap;
	}
	
	@GetMapping(value="/memberFullCheck")
	public boolean crewMemberFullCheck(@RequestParam int crew_idx) {
		
		logger.info("멤버수 체크 실행");
		try {
			logger.info("try 안에서 ! 멤버수 체크 실행");
			if(crew_service.crewMemberFullCheck(crew_idx)) {
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/*
	 * @PostMapping(value="/likeChange") public boolean crewLikeChange(@RequestParam
	 * int crew_idx, @RequestParam String currentStatus) {
	 * 
	 * boolean success = false; Map<String, Object> parmeterMap = new HashMap<>();
	 * parmeterMap.put("crew_idx", crew_idx); parmeterMap.put("currentStatus",
	 * currentStatus);
	 * 
	 * if(currentStatus.equals("N")) { // 좋아요 등록 success =
	 * crew_service.likeRequest(parmeterMap); }else { // 좋아요 취소 success =
	 * crew_service.likeCencel(parmeterMap); } return success; }
	 */

	
}