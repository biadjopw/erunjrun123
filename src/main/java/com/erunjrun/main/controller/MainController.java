package com.erunjrun.main.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.erunjrun.main.service.MainService;

@Controller
public class MainController {

	@Autowired MainService mainService;
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value="/")
	public String main(HttpSession session) {
		session.setAttribute("loginId", "kimee01");
		session.setAttribute("profileImg", "/photo/profile_img1.jpg");
		session.setAttribute("iconImg", "resources/img/icon/icon1.png");
		session.setAttribute("adminYn", "N");
		
		return "main";
	}
	
    @GetMapping(value="/crewWriteView")
    public String crewWriteView() {
        return "crew/crewWrite";
    }
	
	
}
