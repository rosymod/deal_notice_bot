package com.kst.bot.dealnotice.dao;

import com.kst.bot.dealnotice.dto.NoticeKeywordDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeKeywordDao {

    int addKeyword(NoticeKeywordDto noticeKeywordDto);

    int removeKeyword(NoticeKeywordDto noticeKeywordDto);

    List<NoticeKeywordDto> getListKeyword(NoticeKeywordDto noticeKeywordDto);
}
