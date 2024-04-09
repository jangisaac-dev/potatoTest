package dev.hsu.potatotest.controller;

import dev.hsu.potatotest.constants.AuthConstant;
import dev.hsu.potatotest.domain.UserModel;
import dev.hsu.potatotest.domain.VrfKeyModel;
import dev.hsu.potatotest.dto.TokenDTO;
import dev.hsu.potatotest.service.UserService;
import dev.hsu.potatotest.service.VrfKeyService;
import dev.hsu.potatotest.utils.JwtTokenProvider;
import dev.hsu.potatotest.utils.VerifyKeyUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @Parameter(name = "email", description = "email Address"),
            @Parameter(name = "password", description = "need to encrypt")
    })
    @PostMapping("/signUp")
    public ResponseEntity signUp(
            String email,
            String password) {
        UserModel user = userService.createUser(new UserModel(email, password));
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
    public ResponseEntity reVerifyUser(String email) {
        VrfKeyModel origin = vrfKeyService.findByUserEmail(email);
        if (origin != null) {
            vrfKeyService.delete(origin.getId());
        }
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

        return ResponseEntity.ok(jwtTokenProvider.createToken(user.getId()));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = TokenDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "not found user"),
            @ApiResponse(responseCode = "400", description = "not matching password"),
    })
    @Parameters({
            @Parameter(name = "token", description = "token string"),
            @Parameter(name = "updateUserId", description = "userId for update auth"),
            @Parameter(name = "role", description = "role for update.\nIf u want set custom value, input -1~long max value", examples = {
                    @ExampleObject(name = "role for invalidate", value = "invalidate"),
                    @ExampleObject(name = "role for default user", value = "default"),
                    @ExampleObject(name = "role for admin user", value = "admin"),
                    @ExampleObject(name = "role for custom value (invalidate)", value = "-1"),
                    @ExampleObject(name = "role for custom value (default)", value = "100"),
                    @ExampleObject(name = "role for custom value (admin)", value = "1000"),
            }),
            @Parameter(name = "serverPassword", description = "serverPassword"),
    })
    @PostMapping("/updateAuth")
    public ResponseEntity updateAuth(
            @NotEmpty @NotNull String token,
            @NotEmpty @NotNull String updateUserId,
            @NotEmpty @NotNull String role,
            @Nullable String serverPassword
            ) {

        Pair<Integer, String> validChecker = jwtTokenProvider.isTokenValid(token);
        if (validChecker != null) {
            return ResponseEntity.status(validChecker.a).body(validChecker.b);
        }

        Long userId = jwtTokenProvider.getUserId(token);

        boolean isValidPermission = false;

        UserModel requestUserModel = userService.findById(userId);
        if (requestUserModel != null
                && requestUserModel.getUserRole() >= AuthConstant.USER_ROLE_ADMIN
        ) {
            isValidPermission = true;
        }

        if (serverPassword != null
                && verifyKeyUtil.checkAdminPassword(serverPassword)
        ) {
            isValidPermission = true;
        }

        if (!isValidPermission) {
            return ResponseEntity.badRequest().body("Invalid Permission");
        }

        if (!NumberUtils.isParsable(updateUserId)) {
            return ResponseEntity.badRequest().body("Invalid new user id");
        }

        UserModel updateUserModel = userService.findById(Long.valueOf(updateUserId));
        if (updateUserModel == null) {
            return ResponseEntity.badRequest().body("Invalid new user");
        }

        Long longRole = switch (role) {
            case "invalidate" -> -1L;
            case "default" -> 100L;
            case "admin" -> 1000L;
            default -> null;
        };

        if (longRole == null && NumberUtils.isParsable(role)) {
            longRole = Long.parseLong(role);
        }

        if (longRole == null) {
            return ResponseEntity.badRequest().body("Invalid new ROLE");
        }

        return ResponseEntity.ok(userService.updateUserRole(Long.valueOf(updateUserId), longRole));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = @Content(
                    schema = @Schema(implementation = TokenDTO.class)
            )),
            @ApiResponse(responseCode = "400", description = "invalid token"),
            @ApiResponse(responseCode = "404", description = "not found user"),
    })
    @Parameters({
            @Parameter(name = "token", description = "token value"),
    })
    @PostMapping("/reissueToken")
    public ResponseEntity reissueToken(String token) {

        Pair<Integer, String> validChecker = jwtTokenProvider.isTokenValid(token);
        if (validChecker != null) {
            return ResponseEntity.status(validChecker.a).body(validChecker.b);
        }
        Long userId = jwtTokenProvider.getUserId(token);
        UserModel user = userService.findById(userId);

        if (user.getUserRole() < 0) {
            return ResponseEntity.badRequest().body("not verified user");
        }

        return ResponseEntity.ok(jwtTokenProvider.createToken(user.getId()));
    }

}
