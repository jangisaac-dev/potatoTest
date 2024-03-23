package dev.hsu.potatotest.repo;

import dev.hsu.potatotest.domain.ContentModel;
import dev.hsu.potatotest.domain.ContentSingleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContentSingleRepository extends JpaRepository<ContentSingleModel, Long> {

    //select * from content_model
    @Query("select c from content_model c")
    List<ContentSingleModel> findAllQuery();

    @Query("select c from content_model c where c.id = :id")
    Optional<ContentSingleModel> findByIdQuery(Long id);

//    @Query("insert into c values INTO ex VALUES (1, '홍길동', '010-1234-5678', '서울 중랑구 상봉동');")

}
