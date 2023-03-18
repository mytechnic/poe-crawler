package starbin.poe.crawler.gem;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import starbin.poe.crawler.Const;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class CrawlUtils {

    public static String getCrawlData(WebDriver driver, String url) throws InterruptedException, IOException {
        String key = getHash(url);

        File file = new File(System.getProperty("java.io.tmpdir"), "poe-crawler/" + key + ".crawl");
        log.info("url: {}, file: {}", url, file.getAbsolutePath());

        if (file.exists()) {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        }

        driver.get(url);
        Thread.sleep(Const.TICK_TIME);

        FileUtils.write(file, driver.getPageSource(), Charset.defaultCharset());
        return driver.getPageSource();
    }

    public static String getHash(String data) {
        String MD5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte[] byteData = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte byteDatum : byteData) {
                sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }
}
