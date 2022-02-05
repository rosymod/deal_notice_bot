package com.kst.bot.dealnotice.svc;

import com.kst.bot.dealnotice.dao.MemberDao;
import com.kst.bot.dealnotice.dao.NoticeHistoryDao;
import com.kst.bot.dealnotice.dto.DealInfo;
import com.kst.bot.dealnotice.dto.MemberDto;
import com.kst.bot.dealnotice.dto.NoticeHistoryDto;
import com.kst.bot.dealnotice.handler.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NoticeSvc {

    @Inject
    private MemberDao memberDao;

    @Inject
    private NoticeHistoryDao noticeHistoryDao;

    @Inject
    private CrawlingSvc crawlingSvc;

    @Inject
    private MessageHandler messageHandler;

    private final String messagePrifix = "====== 새글알림 ======\n알림대상 : %s\n";

    @Scheduled(fixedDelay = 60000)
    public void noticeSchedule(){
        log.info("== noticeSchedule START");
        String[] keywords;
        for(MemberDto member : memberDao.getNoticeMembers()){
            if(member.getKeyword() != null){
                log.info("== noticeSchedule RUN - member {}, chatId {}, keyword {}", member.getLastName(), member.getChatId(), member.getKeyword());
                try{
                    keywords = member.getKeyword().split(",");
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format(messagePrifix,member.getKeyword()));
                    List<DealInfo> dealList = crawlingSvc.getList(keywords);
                    List<NoticeHistoryDto> historyList = noticeHistoryDao.getNoticeHistoryList(NoticeHistoryDto.builder().memberIdx(member.getIdx()).includes(dealList).build());

                    dealList.removeIf(d ->
                        historyList.stream().anyMatch(h -> d.getTitle().indexOf(h.getContent()) >= 0)
                    );

                    if(dealList.size() > 0){
                        log.info("== noticeSchedule NEW DEAL Send - member {}, chatId {}, keyword {}", member.getLastName(), member.getChatId(), member.getKeyword());
                        messageHandler.appendTextDealList(dealList,sb);
                        messageHandler.sendMessage(member.getChatId(),sb.toString());

                        List<NoticeHistoryDto> noticeHistoryList = new ArrayList<>();
                        for(DealInfo dealInfo : dealList){
                            noticeHistoryList.add(NoticeHistoryDto.builder().memberIdx(member.getIdx()).content(dealInfo.getTitle()).build());
                        }
                        noticeHistoryDao.addNoticeHistory(noticeHistoryList);
                    }else{
                        log.info("== noticeSchedule Not exist NEW DEAL - member {}, chatId {}, keyword {}", member.getLastName(), member.getChatId(), member.getKeyword());
                    }
                }catch(Exception e){
                    log.error("noticeSchedule exception - {}",e.getMessage());
                }
            }else{
                log.info("== noticeSchedule NOT WORK - member {}, chatId {}", member.getLastName(), member.getChatId());
                keywords = null;
            }
        }
    }
}