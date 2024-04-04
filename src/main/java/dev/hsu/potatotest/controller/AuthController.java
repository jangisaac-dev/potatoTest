package dev.hsu.potatotest.controller;

import dev.hsu.potatotest.constants.AuthConstant;
import dev.hsu.potatotest.domain.ContentModel;
import dev.hsu.potatotest.domain.TagModel;
import dev.hsu.potatotest.domain.UserModel;
import dev.hsu.potatotest.domain.VrfKeyModel;
import dev.hsu.potatotest.dto.ContentDTO;
import dev.hsu.potatotest.dto.TokenDTO;
import dev.hsu.potatotest.repo.VrfKeyRepository;
import dev.hsu.potatotest.service.*;
import dev.hsu.potatotest.utils.JwtTokenProvider;
import dev.hsu.potatotest.utils.VerifyKeyUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private VrfKeyService vrfKeyService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private VerifyKeyUtil verifyKeyUtil;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = UserModel.class)
            )),
            @ApiResponse(responseCode = "404", description = "already join user"),
    })
    @Parameters({
            @Parameter(name = "name", description = "name Value"),
            @Parameter(name = "email", description = "email Address"),
            @Parameter(name = "password", description = "need to encrypt")
    })
    @PostMapping("/signUp")
    public ResponseEntity signUp(
            @Nullable String name,
            String email,
            String password) {

        UserModel user = userService.createUser(new UserModel(name, email, password));
        if (user == null) { // 중복 가능성
            return ResponseEntity.badRequest().body("중복된 유저입니다.");
        }

        //원래는 ok만 줘야함.
//        vrfKeyService.createKey(email);
//        return ResponseEntity.ok().build();

        //테스트할때는 key object 통째로 return
        VrfKeyModel model = vrfKeyService.createKey(email);
        return ResponseEntity.ok(model);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
    })
    @Parameters({
            @Parameter(name = "email", description = "email address")
    })
    @PostMapping("/reVerifyUser")
    public ResponseEntity reVerifyUser(
            String email) {
        VrfKeyModel model = vrfKeyService.createKey(email);
        return ResponseEntity.ok(model);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success")
    })
    @Parameters({
            @Parameter(name = "email", description = "email address"),
            @Parameter(name = "key", description = "key Value")
    })
    @PostMapping("/verifyUser")
    public ResponseEntity verifyUser(
            String email,
            String key) {
        VrfKeyModel vrfObj = vrfKeyService.findByUserEmail(email);

        if (vrfObj == null) {
            return ResponseEntity.notFound().build();
        }

        boolean result = vrfObj.getVerifiedKey().equals(key);

        if (!result) {
            return ResponseEntity.badRequest().body("옳지 않은 키입니다.");
        }


        UserModel user = userService.findByUserEmail(vrfObj.getUserEmail());
        if (user == null) {
            return ResponseEntity.badRequest().body("잘못된 유저입니다.");
        }

        vrfKeyService.delete(vrfObj.getId());
        userService.updateUserRole(user.getId(), AuthConstant.USER_ROLE_DEFAULT);

        return ResponseEntity.ok().build();
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = TokenDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "not found user or not matching password"),
            @ApiResponse(responseCode = "400", description = "not verified user"),
    })
    @Parameters({
            @Parameter(name = "email", description = "email Address"),
            @Parameter(name = "password", description = "need to encrypt")
    })
    @PostMapping("/login")
    public ResponseEntity login(
            String email,
            String password) {
        UserModel user = userService.login(email, password);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (user.getUserRole() < 0) {
            return ResponseEntity.badRequest().body("not verified user");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        TokenDTO tokenDTO = new TokenDTO(accessToken, refreshToken);

        return ResponseEntity.ok(tokenDTO);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = TokenDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "not found user"),
            @ApiResponse(responseCode = "400", description = "not matching password"),
    })
    @Parameters({
            @Parameter(name = "email", description = "email Address"),
            @Parameter(name = "password", description = "need to encrypt")
    })
    @PostMapping("/updateAuth")
    public ResponseEntity updateAuth(
            String token,
            String userId,
            String password) {
//        System.out.println("validate : " + jwtTokenProvider.validateToken(token));
//        System.out.println("role : " + jwtTokenProvider.getUserRole(token));
//        System.out.println("extractAllClaims : " + jwtTokenProvider.extractAllClaims(token));


//
//        UserModel user = userService.login(email, password);
//        if (user == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        if (user.getUserRole() < 0) {
//            return ResponseEntity.badRequest().body("not verified user");
//        }
//
//        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
//        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
//        TokenDTO tokenDTO = new TokenDTO(accessToken, refreshToken);
//
//        return ResponseEntity.ok(tokenDTO);
        return ResponseEntity.ok().build();
    }

}
