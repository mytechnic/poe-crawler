package starbin.poe.crawler.gem.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("skill_gem_price")
@Data
public class SkillGemPriceEntity {
    @Id
    private Long priceNo;
    private String name;
    private String tag;
    private Integer level;
    private String quality;
    private boolean isCorrupt;

    private String valueType;
    private Double value;
    private Double last7Day;
    private String listed;
    private LocalDateTime created;
}
