package starbin.poe.crawler.gem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import starbin.poe.crawler.Const;
import starbin.poe.crawler.gem.db.SkillGemEntity;
import starbin.poe.crawler.gem.db.SkillGemEntityRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillGemInfoFinder {
    private final SkillGemEntityRepository skillGemEntityRepository;

    public void init() {
        crawl();
    }

    private void crawl() {
        log.info("SKILL GEM INFO CRAWL START");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, Const.CHROME_DRIVER_EXE_PROPERTY);

        ChromeOptions o = new ChromeOptions();
        o.addArguments("incognito");
        o.addArguments("headless");
        WebDriver driver = new ChromeDriver(o);

        List<String> crawlUrlList = new ArrayList<>();
        crawlUrlList.add("https://poedb.tw/kr/Gem");
        crawlUrlList.add("https://poedb.tw/kr/Active_Skill_Gems");
        crawlUrlList.add("https://poedb.tw/kr/Support_Skill_Gems#%EB%B3%B4%EC%A1%B0%EC%8A%A4%ED%82%AC%EC%A0%AC%EC%A0%AC");

        skillGemEntityRepository.truncate();
        List<SkillGemEntity> SkillGemEntityList = new ArrayList<>();
        try {
            for (String url : crawlUrlList) {
                log.info("crawl url: {}", url);
                Document doc = Jsoup.parse(CrawlUtils.getCrawlData(driver, url));
                Elements elements = doc.select(".tab-content table tr");

                for (Element element : elements) {
                    if (!ObjectUtils.isEmpty(element.select(".tablesorter-headerRow").text())) {
                        continue;
                    }

                    String code = getCode(element.select(".itemclass_gem"));
                    if (SkillGemEntityList.stream().anyMatch(entity -> entity.getCode().equals(code))) {
                        continue;
                    }

                    SkillGemEntity skillGemEntity = new SkillGemEntity();
                    skillGemEntity.setCode(code);
                    skillGemEntity.setName(getEngName(element.select(".itemclass_gem")));
                    skillGemEntity.setKorName(getKorName(element.select(".itemclass_gem")));
                    SkillGemEntityList.add(skillGemEntity);
                }
            }

            for (SkillGemEntity skillGemEntity : SkillGemEntityList) {
                String url = "https://poedb.tw/kr/" + skillGemEntity.getCode();

                Document doc = Jsoup.parse(CrawlUtils.getCrawlData(driver, url));
                Elements elements = doc.select("div .card");
                for (Element element : elements) {
                    if (!element.select("h3").text().startsWith("특이한")) {
                        continue;
                    }

                    Elements eles = element.select("tbody > tr");
                    for (Element ele : eles) {
                        String name = ele.select("td:eq(0)").text().trim();
                        String weight = ele.select("td:eq(2)").text().trim();
                        switch (name) {
                            case "상급" -> skillGemEntity.setBasicWeight(Integer.valueOf(weight));
                            case "상이한" -> skillGemEntity.setDivergentWeight(Integer.valueOf(weight));
                            case "기묘한" -> skillGemEntity.setAnomalousWeight(Integer.valueOf(weight));
                            case "몽환적인" -> skillGemEntity.setPhantasmalWeight(Integer.valueOf(weight));
                        }
                    }

                    skillGemEntity.setWeightCount(0);
                    skillGemEntity.setWeightTotal(0);
                    if (skillGemEntity.getBasicWeight() != null) {
                        skillGemEntity.setWeightCount(skillGemEntity.getWeightCount() + 1);
                        skillGemEntity.setWeightTotal(skillGemEntity.getWeightTotal() + skillGemEntity.getBasicWeight());
                    }
                    if (skillGemEntity.getDivergentWeight() != null) {
                        skillGemEntity.setWeightCount(skillGemEntity.getWeightCount() + 1);
                        skillGemEntity.setWeightTotal(skillGemEntity.getWeightTotal() + skillGemEntity.getDivergentWeight());
                    }
                    if (skillGemEntity.getAnomalousWeight() != null) {
                        skillGemEntity.setWeightCount(skillGemEntity.getWeightCount() + 1);
                        skillGemEntity.setWeightTotal(skillGemEntity.getWeightTotal() + skillGemEntity.getAnomalousWeight());
                    }
                    if (skillGemEntity.getPhantasmalWeight() != null) {
                        skillGemEntity.setWeightCount(skillGemEntity.getWeightCount() + 1);
                        skillGemEntity.setWeightTotal(skillGemEntity.getWeightTotal() + skillGemEntity.getPhantasmalWeight());
                    }
                    skillGemEntityRepository.save(skillGemEntity);
                }
            }

            log.info("SKILL GEM INFO CRAWL END");

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            driver.close();
            driver.quit();
        }
    }

    private String getKorName(Elements ele) {

        String name = ele.text().trim();
        if (ObjectUtils.isEmpty(name)) {
            return "";
        }

        return name;
    }

    private String getEngName(Elements ele) {

        String href = ele.attr("href");
        if (ObjectUtils.isEmpty(href)) {
            return "";
        }

        href = href.replace("/kr/", "");
        href = href.replace("_", " ");

        return href;
    }

    private String getCode(Elements ele) {

        String href = ele.attr("href");
        if (ObjectUtils.isEmpty(href)) {
            return "";
        }

        href = href.replace("/kr/", "");

        return href;
    }
}
