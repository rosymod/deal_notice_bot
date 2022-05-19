package com.kst.bot.dealnotice.svc;

import com.kst.bot.dealnotice.dao.MemberDao;
import com.kst.bot.dealnotice.dao.NoticeHistoryDao;
import com.kst.bot.dealnotice.dao.NoticeKeywordDao;
import com.kst.bot.dealnotice.dto.DealInfo;
import com.kst.bot.dealnotice.dto.MemberDto;
import com.kst.bot.dealnotice.dto.NoticeHistoryDto;
import com.kst.bot.dealnotice.dto.NoticeKeywordDto;
import com.kst.bot.dealnotice.handler.MessageHandler;
import com.kst.bot.dealnotice.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NoticeSvc {

    @Inject
    private MemberDao memberDao;

    @Inject
    private NoticeHistoryDao noticeHistoryDao;

    @Inject
    private NoticeKeywordDao noticeKeywordDao;

    @Inject
    private CrawlingSvc crawlingSvc;

    @Inject
    private MessageHandler messageHandler;

    private final String messagePrifix = "========= 새글알림 ========="; //알림대상 : %s\n";

    @Scheduled(fixedDelay = 60000)
    public void noticeSchedule(){
        log.info("== noticeSchedule START");

        boolean isContinue = true;
        List<NoticeKeywordDto> keywords;
        List<MemberDto> memberList = memberDao.getNoticeMembers();
        List<DealInfo> dealList = null;

        if(null == memberList || memberList.size() < 1){
            isContinue = false;
        }

        if(isContinue){
            dealList = crawlingSvc.getList(); //keywords;
            isContinue = null != dealList && dealList.size() > 0;
        }

        if(isContinue){
            dealList = dealList.stream().distinct().collect(Collectors.toList());
            for(MemberDto member : memberDao.getNoticeMembers()){
                keywords = noticeKeywordDao.getListKeyword(NoticeKeywordDto.builder().memberIdx(member.getIdx()).build());
                log.info("== noticeSchedule RUN - member {} {}, chatId {}, keyword {}", member.getLastName(), member.getFirstName(), member.getChatId(), keywords);
                if(null == keywords || keywords.size() < 1){
                    log.info("== noticeSchedule Empty Keywords - member {} {}, chatId {}", member.getLastName(), member.getFirstName(), member.getChatId());
                    continue;
                }
                try{
                    StringBuilder sb = new StringBuilder();
                    //sb.append(String.format(messagePrifix,member. getKeyword()));
                    List<String> tmpKeyList = keywords.stream().map(k->k.getKeyword()).collect(Collectors.toList());

                    List<DealInfo> memDealList = dealList.stream()
                        .filter(d -> d.getMatchWord().matches(StringUtil.listToMatchStr(tmpKeyList))) // keyword 필터
                        .collect(Collectors.toList());

                    List<NoticeHistoryDto> historyList;
                    log.info("== noticeSchedule After Keyword filter - member {} {}, chatId {}, deal count {}", member.getLastName(), member.getFirstName(), member.getChatId(), memDealList.size());
                    if(memDealList.size() > 0){
                        historyList = noticeHistoryDao.getNoticeHistoryList(NoticeHistoryDto.builder().memberIdx(member.getIdx()).includes(memDealList).build());
                        memDealList = memDealList.stream()
                                .filter(d -> !historyList.stream().anyMatch(h->d.getMatchWord().indexOf(h.getContent()) >= 0)) // history 필터
                                .collect(Collectors.toList());
                    }
                    log.info("== noticeSchedule After history filter - member {} {}, chatId {}, deal count {}", member.getLastName(), member.getFirstName(), member.getChatId(), memDealList.size());
//                    memDealList = memDealList.stream()
//                        .filter(d -> historyList.stream().anyMatch(h->d.getMatchWord().indexOf(h.getContent()) < 0)) // history 필터
//                        .collect(Collectors.toList());

                    if(null != memDealList && memDealList.size() > 0){
                        log.info("== noticeSchedule NEW DEAL Send - member {} {}, chatId {}, keyword {}", member.getLastName(), member.getFirstName(), member.getChatId(), keywords);
                        messageHandler.appendTextDealList(memDealList,sb);
                        messageHandler.sendMessage(member.getChatId(),sb.toString());

                        List<NoticeHistoryDto> noticeHistoryList = new ArrayList<>();
                        for(DealInfo dealInfo : memDealList){
                            noticeHistoryList.add(NoticeHistoryDto.builder().memberIdx(member.getIdx()).content(dealInfo.getMatchWord()).build());
                        }
                        noticeHistoryDao.addNoticeHistory(noticeHistoryList);
                    }else{
                        log.info("== noticeSchedule Not exist NEW DEAL - member {} {}, chatId {}, keyword {}", member.getLastName(), member.getFirstName(), member.getChatId(), keywords);
                    }
                }catch(Exception e){
                    log.error("noticeSchedule exception - {}",e.getMessage());
                }
            }
        }
    }
}