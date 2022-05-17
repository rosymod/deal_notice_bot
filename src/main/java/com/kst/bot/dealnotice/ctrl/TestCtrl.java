package com.kst.bot.dealnotice.ctrl;

import com.kst.bot.dealnotice.svc.CrawlingSvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RequestMapping("/test")
@RestController
public class TestCtrl {

    @Inject
    private CrawlingSvc crawlingSvc;

    @GetMapping("/crawling/get")
    public Object crawlingTest(){
        return crawlingSvc.getList(null);
    }
}
