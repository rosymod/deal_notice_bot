package com.kst.bot.dealnotice.svc;

import com.kst.bot.dealnotice.dao.NoticeKeywordDao;
import com.kst.bot.dealnotice.dto.NoticeKeywordDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
public class NoticeKeywordSvc {

    @Inject
    private NoticeKeywordDao noticeKeywordDao;

    @Transactional
    public boolean addKeyword(int memberIdx, String keyword){
        int row = noticeKeywordDao.addKeyword(NoticeKeywordDto.builder().memberIdx(memberIdx).keyword(keyword).build());
        if(row != 1){
            return false;
        }
        return true;
    }

    @Transactional
    public boolean removeKeyword(int memberIdx, String keyword){
        int row = noticeKeywordDao.removeKeyword(NoticeKeywordDto.builder().memberIdx(memberIdx).keyword(keyword).build());
        if(row <= 0){
            return false;
        }
        return true;
    }

    public List<NoticeKeywordDto> getListKeyword(int memberIdx){
        return noticeKeywordDao.getListKeyword(NoticeKeywordDto.builder().memberIdx(memberIdx).build());
    }
}
