package com.voidforum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor // Necesario para que MongoDB pueda instanciar la clase al leer de la BD
@AllArgsConstructor // Necesario para que @Builder funcione correctamente con @NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password; // Recordá que la lógica del Service debe hashear esto antes de guardar

    private String avatar;

    @CreatedDate
    private LocalDateTime createdAt;
}