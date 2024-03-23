package dev.hsu.potatotest.repo;

import dev.hsu.potatotest.domain.ContentModel;
import dev.hsu.potatotest.domain.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<TagModel, Long> {

    @Query("select t from tag_model t where t.contentModel.id = :id")
    List<TagModel> findAllByContentId(Long id);
}
