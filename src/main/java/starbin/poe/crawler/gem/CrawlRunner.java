package starbin.poe.crawler.gem;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrawlRunner {
    private final SkillGemInfoFinder skillGemInfoFinder;
    private final SkillGemPriceFinder skillGemPriceFinder;

    @PostConstruct
    public void init() {
        skillGemPriceFinder.init();
        //skillGemInfoFinder.init();
    }
}
