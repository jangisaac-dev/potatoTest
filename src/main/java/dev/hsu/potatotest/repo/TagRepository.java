package dev.hsu.potatotest.repo;

import dev.hsu.potatotest.domain.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<TagModel, Long> {

    @Query("select t from tag_model t where t.contentId = :id")
    List<TagModel> findAllByContentId(Long id);

//    @Query("select t from tag_model t where t.tagName = :tagName")
//    List<TagModel> findAllByTagName(String tagName);

    @Query("select distinct t.contentId from tag_model t where t.tagName = :tagName")
    List<Long> findContentIdByTagName(String tagName);
}
