package com.erunjrun.main.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.erunjrun.main.dto.AlarmDTO;

@Mapper
public interface AlarmDAO {

	int alarmCount(String loginId);

	List<AlarmDTO> alarmList(String loginId);

	void crewAdmin(AlarmDTO alarm_dto);

	void crewMemberExpel(Map<String, Object> param);

	void crewNoticeComment(AlarmDTO alarm_dto);

	void boardComment(AlarmDTO alarm_dto);

	void personalChat(AlarmDTO alarm_dto);

	void crewLeaderChat(AlarmDTO alarm_dto);

	void crewGroupChat(AlarmDTO alarm_dto);

	int alarmUseUpdate(Object ala);

	void askCommentAlarm(AlarmDTO alarm_dto);

}
