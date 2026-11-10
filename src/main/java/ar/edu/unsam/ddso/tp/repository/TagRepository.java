package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);

    @Query("select count(d) > 0 from Digitization d join d.tags t where t.id = :tagId")
    boolean isInUse(@Param("tagId") Long tagId);
}