package com.kst.bot.dealnotice.dao;

import com.kst.bot.dealnotice.dto.NoticeHistoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeHistoryDao {

    List<NoticeHistoryDto> getNoticeHistoryList(NoticeHistoryDto noticeHistoryDto);

    int addNoticeHistory(List<NoticeHistoryDto> noticeHistoryList);
}
