package dev.hsu.potatotest.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.context.annotation.Primary;

//foreign key remove version
@Entity(name = "user_model")
@Schema(description = "User Model")
public class UserModel {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String userName;

    @NotNull
    @Column(unique = true)
    private String userEmail;

    @NotNull
    private String userPassword;

    @ColumnDefault("-1")
    private Long userRole;

    public UserModel(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
