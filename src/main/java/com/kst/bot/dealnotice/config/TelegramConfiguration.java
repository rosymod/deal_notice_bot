package com.kst.bot.dealnotice.config;

import com.kst.bot.dealnotice.handler.MessageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Configuration
public class TelegramConfiguration {

    @Value("${cnf.telegram.isUse}")
    private boolean isUse;

    @Inject
    private MessageHandler messageHandler;

    @PostConstruct
    public void init(){
        if(isUse){
            TelegramBotsApi telegramApi = null;
            try {
                telegramApi = new TelegramBotsApi(DefaultBotSession.class);
                telegramApi.registerBot(messageHandler);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
