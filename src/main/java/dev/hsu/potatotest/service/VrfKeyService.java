package dev.hsu.potatotest.service;

import dev.hsu.potatotest.domain.VrfKeyModel;
import dev.hsu.potatotest.repo.VrfKeyRepository;
import dev.hsu.potatotest.utils.JwtTokenProvider;
import dev.hsu.potatotest.utils.VerifyKeyUtil;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class VrfKeyService {

    @Autowired
    private VerifyKeyUtil verifyKeyUtil;

    @Autowired
    private VrfKeyRepository vrfKeyRepository;

    public VrfKeyModel createKey(String email) {
        return vrfKeyRepository.save(new VrfKeyModel(email, verifyKeyUtil.generateKey()));
    }

    public Pair<Boolean, String> verifyKey(String email, String key) {
        Optional<VrfKeyModel> opModel = vrfKeyRepository.findByUserEmail(email);
        if (opModel.isEmpty()) {
            return new Pair<>(false, "not found e-mail");
        }

        boolean result = opModel.get().getVerifiedKey().equals(key);
        if (!result) {
            return new Pair<>(false, "key not matching");
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        long checkNow = now.getTime() - (verifyKeyUtil.getVerifyTime() * 1000);
        long creationTime = Timestamp.valueOf(opModel.get().getCreatedDate()).getTime();
        if (checkNow > creationTime) {
            return new Pair<>(false, "deprecated key 'time over'");
        }

        vrfKeyRepository.delete(opModel.get());
        return new Pair<>(true, "verified success");
    }

    public VrfKeyModel findByUserEmail(String email) {
        Optional<VrfKeyModel> result = vrfKeyRepository.findByUserEmail(email);
        if (result.isEmpty()) {
            return null;
        }
        return result.get();
    }

    public boolean delete(Long id) {
        if (vrfKeyRepository.findById(id).isEmpty()) {
            return false;
        }
        vrfKeyRepository.deleteById(id);
        return true;
    }
}
