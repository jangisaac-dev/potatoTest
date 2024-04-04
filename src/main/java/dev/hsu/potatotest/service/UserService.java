package dev.hsu.potatotest.service;

import com.google.gson.Gson;
import dev.hsu.potatotest.domain.TagModel;
import dev.hsu.potatotest.domain.UserModel;
import dev.hsu.potatotest.repo.TagRepository;
import dev.hsu.potatotest.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public UserModel findById(Long id) {
        Optional<UserModel> result = userRepository.findById(id);
        if (result.isEmpty()) {
            return null;
        }
        return result.get();
    }

    public UserModel findByUserEmail(String email) {
        Optional<UserModel> result = userRepository.findByUserEmail(email);
        if (result.isEmpty()) {
            return null;
        }
        return result.get();
    }

    public UserModel createUser(UserModel userModel) {
        if (userRepository.findByUserEmail(userModel.getUserEmail()).isPresent()) {
            return null;
        }
        System.out.println("userModel : " + new Gson().toJson(userModel));
        return userRepository.save(userModel);
    }

    public UserModel login(String email, String pw) {
        Optional<UserModel> opUserModel = userRepository.findByUserEmail(email);
        if (opUserModel.isEmpty()) {
            return null;
        }

        return opUserModel.get().getUserPassword().equals(pw) ? opUserModel.get() : null;
    }

    public UserModel updateUserRole(Long id, Long role) {
        Optional<UserModel> opUserModel = userRepository.findById(id);
        if (opUserModel.isEmpty()) {
            return null;
        }

        UserModel ovrModel = opUserModel.get();
        ovrModel.setUserRole(role);
        return userRepository.saveAndFlush(ovrModel);
    }
}
