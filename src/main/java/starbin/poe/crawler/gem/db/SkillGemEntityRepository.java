package starbin.poe.crawler.gem.db;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface SkillGemEntityRepository extends CrudRepository<SkillGemEntity, Long> {

    @Modifying
    @Query(value = "truncate table skill_gem")
    void truncate();
}
