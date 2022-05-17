package com.kst.bot.dealnotice.svc;

import com.kst.bot.dealnotice.dto.DealInfo;
import com.kst.bot.dealnotice.dto.NoticeKeywordDto;
import com.kst.bot.dealnotice.util.StringUtil;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class CrawlingSvc {

    @Value("${cnf.crawling.targets}")
    private String[] targets;

    @Inject
    private Environment environment;

    public List<DealInfo> getList(List<NoticeKeywordDto> keywordList){
        List<DealInfo> dealList = new ArrayList<>();
        String url;
        for(String target : targets){
            try{
                url = environment.getProperty(String.format("cnf.crawling.detail.%s.host",target)) + environment.getProperty(String.format("cnf.crawling.detail.%s.path",target));
                try {
                    Document document = Jsoup.connect(url).get();
                    dealList.addAll(makeDealInfo(target,document));
                    //log.info("getList - {}",String.valueOf(document));
                } catch (IOException e) {
                    e.printStackTrace();
                    log.info("getList - exception {}",e.getMessage());
                }
            }catch(Exception e){
                log.error("getList - exception target {}, message {}",target, e.getMessage());
            }
        }
        if(dealList.size() > 0 && keywordList != null && keywordList.size() > 0){
            String[] keywords = keywordList.stream().map(i -> i.getKeyword()).collect(Collectors.toList()).stream().toArray(String[]::new);
            String matchStr = ".*(?i)"+String.join(".*|.*(?i)",keywords)+".*";// ".*a.*|.*b.*";
            dealList = dealList.stream().filter(deal -> deal.getMatchWord().matches(matchStr)).collect(Collectors.toList());
        }
        return dealList;
    }

    private List<DealInfo> makeDealInfo(String type, Document document){
        List<DealInfo> list = new ArrayList<>();
        Elements elements = null;
        switch(type){
            case "quasarzon":
                elements = document.select("table tbody tr div[class='market-info-list']");
                for(Element element : elements){
                    if(!"종료".equals(element.select("p[class='tit'] span").first().text())){
                        list.add(DealInfo.builder()
                                .type(type)
                                .title(element.select("p[class='tit'] a span").first().text())
                                .matchWord(StringUtil.removeSpecWord(element.select("p[class='tit'] a span").first().text()))
                                .price(element.select("div[class='market-info-sub'] p").first().select("span span").text().replaceAll("[^0-9]", "").replaceAll("\\B(?=(\\d{3})+(?!\\d))", ","))
                                .link(environment.getProperty(String.format("cnf.crawling.detail.%s.host",type)) + element.select("a").attr("href"))
                                .time(element.select("div[class='market-info-sub'] span[class='date']").first().text().trim())
                                .build());
                    }
                }
                break;
            case "clien":
                elements = document.select("div[class='contents_jirum'] div[class='list_item symph_row jirum']");
                for(Element element : elements){
//                    if(!"종료".equals(element.select("p[class='tit'] span").first().text())){
                        list.add(DealInfo.builder()
                                .type(type)
                                .title(element.select("span[class='list_subject']").first().attr("title"))
                                .matchWord(StringUtil.removeSpecWord(element.select("span[class='list_subject']").first().attr("title")))
                                .link(environment.getProperty(String.format("cnf.crawling.detail.%s.host",type)) + element.select("a").attr("href"))
                                .time(element.select("span[class='timestamp']").first().text().trim())
                                .build());
//                    }
                }
                break;
            case "clien-jungo":
                elements = document.select("div[class='list_content'] div[class='list_item symph_row']");
                for(Element element : elements){
                    if(!"거래완료".equals(element.select("a[class='list_subject'] span").first().text())){
                        list.add(DealInfo.builder()
                                .type(type)
                                .title(element.select("span[class='subject_fixed']").first().text())
                                .matchWord(StringUtil.removeSpecWord(element.select("span[class='subject_fixed']").first().text()))
                                .link(environment.getProperty(String.format("cnf.crawling.detail.%s.host",type)) + element.select("a").attr("href"))
                                .time(element.select("span[class='timestamp']").first().text().trim())
                                .build());
                    }
                }
                break;
            case "ruliweb":
                elements = document.select("table tbody tr");
                for(Element element : elements){
//                    if(!"종료".equals(element.select("p[class='tit'] span").first().text())){
                    list.add(DealInfo.builder()
                            .type(type)
                            .title(element.select("td[class='subject'] a[class='deco']").first().text())
                            .matchWord(element.select("td[class='subject'] a[class='deco']").first().text())
                            .link(element.select("td[class='subject'] a[class='deco']").attr("href"))
                            .time(element.select("td[class='time']").first().text().trim())
                            .build());
//                    }
                }
                break;
            default:
        }
        return list;
    }
}
