package com.erunjrun.member.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erunjrun.member.dto.MemberDTO;

@Mapper
public interface MemberDAO {

	int join(Map<String, String> params);

	int idOverlay(String id);

	int nickNameOverlay(String nickName);

	int emailOverlay(String email);

	int login(String id, String pw);

	MemberDTO findMemberId(MemberDTO dto);

	MemberDTO findMemberPw(MemberDTO dto);

	void updatePw(String id, String tempPw);

}