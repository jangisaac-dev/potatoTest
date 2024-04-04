package dev.hsu.potatotest.domain;

import dev.hsu.potatotest.constants.AuthConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.context.annotation.Primary;

//foreign key remove version
@Entity(name = "user_model")
@Schema(description = "User Model")
@DynamicInsert
public class UserModel {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String userEmail;

    @NotNull
    private String userPassword;

    /**
     * ColumnDefault value : AuthConstant.USER_ROLE_NOT_VERIFIED
     * */
    @ColumnDefault(value = "-1")
    private Long userRole;

    public UserModel() {
    }

    public UserModel(String userEmail, String userPassword) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Long getUserRole() {
        return userRole;
    }

    public void setUserRole(Long userRole) {
        this.userRole = userRole;
    }
}
