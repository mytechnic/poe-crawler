package starbin.poe.crawler.gem.db;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface SkillGemPriceEntityRepository extends CrudRepository<SkillGemPriceEntity, Long> {

    @Modifying
    @Query(value = "truncate table skill_gem_price")
    void truncate();
}
