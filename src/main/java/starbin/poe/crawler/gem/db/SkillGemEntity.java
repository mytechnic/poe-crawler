package starbin.poe.crawler.gem.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("skill_gem")
@Data
public class SkillGemEntity {
    @Id
    private Long gemNo;
    private String code;
    private String name;
    private String korName;
    private Integer basicWeight;
    private Integer divergentWeight;
    private Integer anomalousWeight;
    private Integer phantasmalWeight;
    private Integer weightTotal;
    private Integer weightCount;
}
