package com.kst.bot.dealnotice.handler;

import com.kst.bot.dealnotice.dto.DealInfo;
import com.kst.bot.dealnotice.dto.MemberDto;
import com.kst.bot.dealnotice.resource.BaseConstract;
import com.kst.bot.dealnotice.svc.CrawlingSvc;
import com.kst.bot.dealnotice.svc.MemberSvc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class MessageHandler extends TelegramLongPollingBot {

    @Inject
    private Environment environment;

    @Value("${cnf.telegram.apiKey}")
    private String apiKey;

    @Value("${cnf.telegram.botName}")
    private String botName;

    private String[] allowCommands = new String[]{"/start","/all","/find","/listen","/listeninfo"};

    @Inject
    private CrawlingSvc crawlingSvc;

    @Inject
    private MemberSvc memberSvc;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return apiKey;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        //log.info("onUpdateReceived - received message {}",message);
        if(message == null){
            try{
                log.info("onUpdateReceived - member exit chatId {}", update.getMyChatMember().getFrom().getId().toString());
                exit(update.getMyChatMember().getFrom().getId().toString());
            }catch(Exception e){
                log.error("onUpdateReceived - member exit exception {}",e.getMessage());
            }
        }else{
            log.info("onUpdateReceived - message chatId {}, text {}", message.getChatId().toString(), message.getText() );
            String text = null;
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());

            boolean isSupported = false;
            if(StringUtils.hasText(message.getText()) && message.getText().indexOf("/") == 0 && message.getText().split(" ").length > 0){
                for(String command : allowCommands){
                    if(command.equals(message.getText().split(" ")[0])){
                        isSupported = true;
                        //text = processing(message.getChatId().toString(), command, message.getText().replaceAll(command,"").trim());
                        text = processing(command, message);
                        break;
                    }
                }
            }
            if(!isSupported){
                text = "잘못된 요청입니다 커맨드 정보를 확인해주세요.";
            }

            sendMessage.setText(text);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error("onUpdateReceived - exception {}", e.getMessage());
            }
        }
    }

    private String processing(String command, Message message){
        log.info("processing - chatId {}, command {}", message.getChatId().toString(), command);

        StringBuilder sb = new StringBuilder();
        List<DealInfo> list = null;
        String value = message.getText().replaceAll(command,"").trim();
        MemberDto member = memberSvc.getMember(message.getChatId().toString());

        switch(command){
            case "/start":
                if(member == null){
                    if(memberSvc.addMember(message.getChatId().toString(),message.getFrom().getLastName(),message.getFrom().getFirstName(),message.getFrom().getLanguageCode())){
                        sb.append(message.getFrom().getFirstName());
                        sb.append("님 딜 알림 사용을 환영합니다.");
                        sb.append("\n- 본 채널은 특정 제품의 deal정보가 게시되면 해당 정보를 조회 및 알림설정을 서비스 합니다.");
                        sb.append("\n- 다양한 채널의 정보를 모아서 제공하는것을 목표로 하며, 알림 등록으로 간편하게 정보를 제공받을 수 있는 특징이 있습니다.");
                        sb.append("\n- 일회성 조회 및 알림 등록으로 지속적인 정보 제공이 가능하며 자세한 기능은 명령어 설명을 확인 후 사용해주시길 바랍니다.");
                        sb.append("\n- 일회성 조회 기능의 경우 각 채널의 첫 페이지 정보만 조회하고 있습니다.");
                        sb.append("\n- 현재 제공되는 채널은 클리앙-알뜰구매, 퀘이사존-타세요, 루리웹-핫딜 예판/유저 3곳 입니다.");
                    }else{
                        sb.append("사용자 등록에 실패하였습니다.");
                        sb.append("\n- 사용자 등록 명령어(/start)를 통해 다시 시도해 주시길 바랍니다.");
                        sb.append("\n- 해당 증상이 반복될 경우 관리자에게 문의 바랍니다.");
                    }
                }else if(member != null && BaseConstract.YES.equals(member.getUseYn())) {
                    sb.append(message.getFrom().getFirstName());
                    sb.append("님은 이미 등록된 사용자이며, 등록이 필요하지 않습니다.");
                }else if(member != null || BaseConstract.NO.equals(member.getUseYn())){
                    if(memberSvc.editMember(member.getIdx(),null,null,BaseConstract.YES)){
                        sb.append(message.getFrom().getFirstName());
                        sb.append("님 돌아오신것을 환영합니다.");
                        sb.append("\n- 기존 설정 그대로 사용하실 수 있습니다.");
                    }else{
                        sb.append("사용자 활성화를 실패하였습니다.");
                        sb.append("\n- 사용자 등록 명령어(/start)를 통해 다시 시도해 주시길 바랍니다.");
                        sb.append("\n- 해당 증상이 반복될 경우 관리자에게 문의 바랍니다.");
                    }
                }
                break;
            case "/listen":
                if(!StringUtils.hasText(value) || value.split(",").length <= 0){
                    sb.append("요청한 알림대상이 잘못 입력되었습니다. ");
                    sb.append("\n입력값은 필수이며, 여러개 등록을 원할 경우 ,를 통해 구분합니다.");
                    sb.append("\nex) 맥북,3080,젤다");
                    sb.append("\n현재 입력값 : [ ");
                    sb.append(value);
                    sb.append(" ]");
                }else{
                    if(member == null || BaseConstract.NO.equals(member.getUseYn())){
                        sb.append("등록되지 않은 사용자이거나 비활성화된 사용자 입니다.");
                        sb.append("\n- 사용자 등록(/start) 진행 후 다시 시도해주세요.");
                    }else{
                        if(memberSvc.editMember(member.getIdx(), value,null,null)){
                            sb.append("알림이 등록되었습니다 : ");
                            sb.append(value);
                        }else{
                            sb.append("알림 등록을 실패하였습니다.");
                            sb.append("\n- 해당 증상이 반복될 경우 관리자에게 문의 바랍니다.");
                        }
                    }
                }
                break;
            case "/listeninfo":
                if(member == null || BaseConstract.NO.equals(member.getUseYn())){
                    sb.append("등록되지 않은 사용자이거나 비활성화된 사용자 입니다.");
                    sb.append("\n- 사용자 등록(/start) 진행 후 다시 시도해주세요.");
                }else{
                    sb.append("현재 등록된 알림은 [ ");
                    sb.append(member.getKeyword() == null ? "" : member.getKeyword());
                    sb.append(" ] 입니다.");
                }
                break;
            default:
                sb.append("지원하지 않는 명령어입니다.");
                break;
            case "/find": //allowCommands[1]: // find
                if(!StringUtils.hasText(value)){
                    sb.append("검색어를 올바르게 입력해 주세요.");
                }else{
                    list = crawlingSvc.getList(value);
                    if(list == null || list.size() == 0){
                        sb.append("검색 결과가 없습니다.");
                    }else{
                        sb.append("========= 검색어 [ ");
                        sb.append(value);
                        sb.append(" ] =========");
                        appendTextDealList(list, sb);
                    }
                }
                break;
            case "/all": //allowCommands[0]:  //all
                list = crawlingSvc.getList();

                if(list == null || list.size() == 0){
                    sb.append("조회 결과가 없습니다.");
                }else{
                    sb.append("========= 전체조회 =========");
                    appendTextDealList(list, sb);
                }
                break;
        }
        return sb.toString();
    }

    private void exit(String chatId){
        MemberDto member = memberSvc.getMember(chatId);
        if(member != null){
            try{
                memberSvc.editMember(member.getIdx(),null,null,BaseConstract.NO);
            }catch(Exception e){
                log.error("exit exception - idx {}, detail {}", member.getIdx(), e.getMessage());
            }
        }
    }

    public void appendTextDealList(List<DealInfo> dealList, StringBuilder sb){
        sb.append("\n\n");
        int hiddenCnt = 0;
        StringBuilder temp;
        for(DealInfo dealInfo : dealList){
            if(hiddenCnt > 0) {
                hiddenCnt++;
            }else{
                temp = new StringBuilder();
                temp.append("<< ");
                temp.append(environment.getProperty(String.format("cnf.crawling.detail.%s.name",dealInfo.getType())));
                temp.append(" >> ");
                temp.append(
                        dealInfo.getTitle() != null && dealInfo.getTitle().length() > 50
                                ? dealInfo.getTitle().substring(0,46) + "..."
                                : dealInfo.getTitle());
                temp.append("\n");
                if(StringUtils.hasText(dealInfo.getPrice())){
                    temp.append("- ￦");
                    temp.append(dealInfo.getPrice());
                    temp.append(" | ");
                }
                temp.append(dealInfo.getTime());
                temp.append("\n");
                temp.append(dealInfo.getLink());
                temp.append("\n");

                if(sb.length() + temp.length() >= 4000){
                    hiddenCnt++;
                }else{
                    sb.append(temp);
                }
            }
        }

        if(hiddenCnt > 0){
            sb.append("\n###### 메시지 제한으로 노출하지 못한 컨텐츠 : ");
            sb.append(hiddenCnt);
            sb.append(" ######\n\n");
        }
        //return sb;
    }

    public void sendMessage(String chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}