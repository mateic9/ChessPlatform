package org.example.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_seq")
    private Long id;

    @NotNull
    @Column(name="username",unique = true)
    private String username;

    @Email(message = "Invalid email format")
    @Column(unique=true)
    private  String gmail;
    private String enc_password;
}
