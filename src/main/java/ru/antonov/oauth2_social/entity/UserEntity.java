package ru.antonov.oauth2_social.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Pattern(
            regexp = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)?@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Неправильный формат email"
    )
    @NotBlank(message = "Поле email не может быть пустым")
    @Size(max = 50, message = "Длина поля email не может превышать 50 символов")
    private String email;

    @NotBlank(message = "Поле role не может быть пустым")
    private String role;
}
