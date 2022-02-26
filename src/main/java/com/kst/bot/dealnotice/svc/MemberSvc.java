package com.kst.bot.dealnotice.svc;

import com.kst.bot.dealnotice.dao.MemberDao;
import com.kst.bot.dealnotice.dto.MemberDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.inject.Inject;

@Service
public class MemberSvc {

    @Inject
    private MemberDao memberDao;

    public MemberDto getMember(String chatId){
        try{
            if(!StringUtils.hasText(chatId)){
                return null;
            }
            return memberDao.getMember(MemberDto.builder().chatId(chatId).build());
        }catch(Exception e){
            return null;
        }
    }

    @Transactional
    public boolean addMember(String chatId, String lastName, String firstName, String langCd){
        if(memberDao.addMember(MemberDto.builder().chatId(chatId).lastName(lastName).firstName(firstName).langCd(langCd).build()) <= 0){
            return false;
        }
        return true;
    }

    @Transactional
    public boolean editMember(Integer idx, String langCd, String useYn){
        if(memberDao.editMember(MemberDto.builder().idx(idx).langCd(langCd).useYn(useYn).build()) <= 0){
            return false;
        }
        return true;
    }
}
