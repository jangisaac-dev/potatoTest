package dev.hsu.potatotest.repo;

import dev.hsu.potatotest.domain.TagModel;
import dev.hsu.potatotest.domain.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    @Query("select u from user_model u where u.userEmail = :email")
    Optional<UserModel> findByUserEmail(String email);

}
