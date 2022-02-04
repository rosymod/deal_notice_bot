package com.kst.bot.dealnotice.handler;

import com.kst.bot.dealnotice.dto.DealInfo;
import com.kst.bot.dealnotice.svc.CrawlingSvc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@Component
public class MessageHandler extends TelegramLongPollingBot {

    @Value("${cnf.telegram.apiKey}")
    private String apiKey;

    @Value("${cnf.telegram.botName}")
    private String botName;

    private String[] allowCommands = new String[]{"/all","/find","/listen","/listeninfo"};

    @Inject
    private CrawlingSvc crawlingSvc;

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
        String text = null;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());

        if(!StringUtils.hasText(message.getText()) || message.getText().indexOf("/") != 0){
            text = "잘못된 요청";
        }else{
            boolean isSupported = false;
            for(String command : allowCommands){
                if(message.getText().indexOf(command) == 0 ){
                    isSupported = true;
                    text = makeText(command, message.getText().replaceAll(command,"").trim());
                    break;
                }
            }

            if(!isSupported){
                text = "잘못된 요청";
            }
        }
        //sendMessage.setText("응답");
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("onUpdateReceived - exception {}", e.getMessage());
        }
    }

    private String makeText(String command, String value){
        StringBuilder sb = new StringBuilder();
        List<DealInfo> list = null;
        switch(command){
            case "/all": //allowCommands[0]:  //all
                list = crawlingSvc.getList();
                if(list == null || list.size() == 0){
                    sb.append("조회 결과가 없습니다.");
                }else{
                    sb.append("======= 전체조회 =======");
                    sb.append("\n\n");
                    for(DealInfo dealInfo : list){
                        sb.append(dealInfo.getTitle());
                        sb.append("\n- ￦");
                        sb.append(dealInfo.getPrice());
                        sb.append(" | ");
                        sb.append(dealInfo.getTime());
                        sb.append("\n");
                        sb.append(dealInfo.getLink());
                        sb.append("\n");
                    }
                }
                break;
            case "/find": //allowCommands[1]: // find
                if(!StringUtils.hasText(value)){
                    sb.append("검색어를 입력해주세요");
                }else{
                    list = crawlingSvc.getList(value);
                    if(list == null || list.size() == 0){
                        sb.append("검색 결과가 없습니다.");
                    }else{
                        sb.append("======= 검색 : ");
                        sb.append(value);
                        sb.append(" =======\n\n");
                        for(DealInfo dealInfo : list){
                            sb.append(dealInfo.getTitle());
                            sb.append("\n- ￦");
                            sb.append(dealInfo.getPrice());
                            sb.append(" | ");
                            sb.append(dealInfo.getTime());
                            sb.append("\n");
                            sb.append(dealInfo.getLink());
                            sb.append("\n");
                        }
                    }
                }
                break;
            case "/listen":
            case "/listeninfo":
                sb.append("아직 지원하지 않습니다.");
                break;
            default:
        }
        return sb.toString();
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
