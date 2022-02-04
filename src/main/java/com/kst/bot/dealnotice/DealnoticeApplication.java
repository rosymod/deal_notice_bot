package com.kst.bot.dealnotice;

import com.kst.bot.dealnotice.handler.MessageHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class DealnoticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DealnoticeApplication.class, args);
	}

	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

}
