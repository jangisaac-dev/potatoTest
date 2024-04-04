package dev.hsu.potatotest.repo;

import dev.hsu.potatotest.domain.UserModel;
import dev.hsu.potatotest.domain.VrfKeyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VrfKeyRepository extends JpaRepository<VrfKeyModel, Long> {

    @Query("select m from vrf_key_model m where m.userEmail = :email")
    Optional<VrfKeyModel> findByUserEmail(String email);

}
