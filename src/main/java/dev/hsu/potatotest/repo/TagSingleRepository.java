package dev.hsu.potatotest.repo;

import dev.hsu.potatotest.domain.TagModel;
import dev.hsu.potatotest.domain.TagSingleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagSingleRepository extends JpaRepository<TagSingleModel, Long> {

    @Query("select t from tag_single_model t where t.contentId = :id")
    List<TagSingleModel> findAllByContentId(Long id);

    @Query("select t from tag_single_model t where t.tagName = :tagName")
    List<TagSingleModel> findAllByTagName(String tagName);

    @Query("select distinct t.contentId from tag_single_model t where t.tagName = :tagName")
    List<Long> findContentIdByTagName(String tagName);
}
