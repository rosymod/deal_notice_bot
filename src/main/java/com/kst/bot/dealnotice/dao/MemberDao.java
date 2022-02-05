package com.kst.bot.dealnotice.dao;

import com.kst.bot.dealnotice.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberDao {

    int addMember(MemberDto memberDto);

    int editMember(MemberDto memberDto);

    MemberDto getMember(MemberDto memberDto);

    List<MemberDto> getNoticeMembers();

}
