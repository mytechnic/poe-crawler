package starbin.poe.crawler.gem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import starbin.poe.crawler.Const;
import starbin.poe.crawler.gem.db.SkillGemPriceEntity;
import starbin.poe.crawler.gem.db.SkillGemPriceEntityRepository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillGemPriceFinder {
    private final SkillGemPriceEntityRepository skillGemPriceEntityRepository;

    public void init() {
        crawl(true);
    }

    private void crawl(boolean isAll) {
        log.info("SKILL GEM PRICE CRAWL START");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, Const.CHROME_DRIVER_EXE_PROPERTY);

        ChromeOptions o = new ChromeOptions();
        o.addArguments("incognito");
        o.addArguments("headless");
        WebDriver driver = new ChromeDriver(o);

        try {
            String url = "https://poe.ninja/challenge/skill-gems";
            Document doc = Jsoup.parse(getCrawlData(driver, url));
            Elements elements = doc.select(".cursor-pointer");

            log.debug("검색 건수: {}", elements.size());
            skillGemPriceEntityRepository.truncate();
            for (Element element : elements) {
                SkillGemPriceEntity skillGemPriceEntity = new SkillGemPriceEntity();
                skillGemPriceEntity.setName(getName(element.select(".css-106k4h2")));
                skillGemPriceEntity.setTag(getTag(element.select(".css-106k4h2")));
                skillGemPriceEntity.setLevel(getLevel(element.select("td:eq(1)")));
                skillGemPriceEntity.setQuality(getQuality(element.select("td:eq(2)")));
                skillGemPriceEntity.setCorrupt(isCorrupt(element.select("td:eq(3)")));
                skillGemPriceEntity.setValueType(getValueType(element.select("td:eq(4)")));
                skillGemPriceEntity.setValue(getValue(element.select("td:eq(4)")));
                skillGemPriceEntity.setLast7Day(getLast7Day(element.select("td:eq(5) span span")));
                skillGemPriceEntity.setListed(getListed(element.select("td:eq(6)")));
                skillGemPriceEntity.setCreated(LocalDateTime.now());
                skillGemPriceEntityRepository.save(skillGemPriceEntity);
            }
            log.info("SKILL GEM PRICE CRAWL END");

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            driver.close();
            driver.quit();
        }
    }

    private static String getBaseTag(String name) {
        if (name.startsWith("Awakened")) {
            return "각성한";
        } else if (name.startsWith("Divergent")) {
            return "상이한";
        } else if (name.startsWith("Anomalous")) {
            return "기묘한";
        } else if (name.startsWith("Phantasmal")) {
            return "몽환적인";
        } else {
            return "기본";
        }
    }

    private static String getBaseName(String name) {
        name = name.replace("Divergent ", "");
        name = name.replace("Anomalous ", "");
        name = name.replace("Phantasmal ", "");
        return name;
    }

    private String getName(Elements ele) {

        String name = ele.text().trim();
        if (ObjectUtils.isEmpty(name)) {
            return "";
        }

        return getBaseName(name);
    }

    private String getTag(Elements ele) {

        String tag = ele.text().trim();
        if (ObjectUtils.isEmpty(tag)) {
            return "";
        }

        return getBaseTag(tag);
    }

    private Integer getLevel(Elements ele) {

        String level = ele.text().trim();
        if (ObjectUtils.isEmpty(level)) {
            return -1;
        }

        return Integer.valueOf(level);
    }

    private String getQuality(Elements ele) {

        String quality = ele.text().trim();
        if (ObjectUtils.isEmpty(quality)) {
            return "";
        }

        return quality;
    }

    private boolean isCorrupt(Elements ele) {
        String corrupt = ele.text().trim();
        return "yes".equalsIgnoreCase(corrupt);
    }

    private String getValueType(Elements ele) {
        ele = ele.select("img[title='Divine Orb']");
        return !ObjectUtils.isEmpty(ele) ? "Divine" : "Chaos";
    }

    private Double getValue(Elements ele) {
        String value = ele.text().trim();
        if (ObjectUtils.isEmpty(value)) {
            return -1d;
        }

        return Double.parseDouble(value);
    }

    private Double getLast7Day(Elements ele) {
        String last7day = ele.text().trim();
        if (ObjectUtils.isEmpty(last7day)) {
            return -1d;
        }

        last7day = last7day.replace("%", "").replace("+", "").trim();
        return Double.parseDouble(last7day);
    }

    private String getListed(Elements ele) {
        String listed = ele.text().trim();
        listed = listed.replace("~", "");
        if (listed.contains("k")) {
            listed = listed.replace("k", "");
            listed = String.valueOf(Integer.parseInt(listed) * 1000);
        }
        return listed;
    }

    public static String getCrawlData(WebDriver driver, String url) throws InterruptedException, IOException {
        String key = CrawlUtils.getHash(url);

        File file = new File(System.getProperty("java.io.tmpdir"), "price_" + key + ".crawl");
//        if (file.exists()) {
//            return FileUtils.readFileToString(file, Charset.defaultCharset());
//        }

        driver.get(url);
        Thread.sleep(Const.TICK_TIME);

        for (int i = 0; i < 20; i++) {
            try {
                WebElement button = driver.findElement(By.cssSelector(".css-3pd7rn"));
                button.sendKeys("\n");
                Thread.sleep(Const.TICK_TIME);
            } catch (NoSuchElementException e) {
                break;
            }
        }

        FileUtils.write(file, driver.getPageSource(), Charset.defaultCharset());
        return driver.getPageSource();
    }
}
