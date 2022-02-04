package com.kst.bot.dealnotice.svc;

import com.kst.bot.dealnotice.dto.DealInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CrawlingSvc {

    @Value("${cnf.crawling.targets}")
    private String[] targets;

    @Inject
    private Environment environment;

    public List<DealInfo> getList(String...keyword){
        List<DealInfo> dealList = new ArrayList<>();
        String url;
        for(String target : targets){
            url = environment.getProperty(String.format("cnf.crawling.detail.%s.host",target)) + environment.getProperty(String.format("cnf.crawling.detail.%s.path",target));
            try {
                Document document = Jsoup.connect(url).get();
                dealList.addAll(makeDealInfo(target,document));
                //log.info("getList - {}",String.valueOf(document));
            } catch (IOException e) {
                e.printStackTrace();
                log.info("getList - exception {}",e.getMessage());
            }
        }
        if(dealList.size() > 0 && keyword != null && keyword.length > 0){
            String matchStr = ".*"+String.join(".*|.*",keyword)+".*";// ".*a.*|.*b.*";
            dealList = dealList.stream().filter(deal -> deal.getTitle().matches(matchStr)).collect(Collectors.toList());
        }
        return dealList;
    }

    private List<DealInfo> makeDealInfo(String type, Document document){
        List<DealInfo> list = new ArrayList<>();
        switch(type){
            case "quasarzon":
                Elements elements = document.select("table tbody tr div[class='market-info-list']");
                for(Element element : elements){
                    if(!"종료".equals(element.select("p[class='tit'] span").first().text())){
                        list.add(DealInfo.builder()
                                .type(type)
                                .title(element.select("p[class='tit'] a span").first().text())
                                .price(element.select("div[class='market-info-sub'] p").first().select("span span").text().replaceAll("[^0-9]", "").replaceAll("\\B(?=(\\d{3})+(?!\\d))", ","))
                                .link(environment.getProperty(String.format("cnf.crawling.detail.%s.host",type)) + element.select("a").attr("href"))
                                .time(element.select("div[class='market-info-sub'] span[class='date']").first().text().trim())
                                .build());
                    }
                }
                break;
            default:
        }
        return list;
    }
}
