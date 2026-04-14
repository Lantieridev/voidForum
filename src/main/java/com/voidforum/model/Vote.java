package com.voidforum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Esta etiqueta le dice a la base de datos que este objeto es como una "ficha" que se guardará en una carpeta llamada "votes".
@Document(collection = "votes")
// Esta es una "regla de seguridad" que le dice a la base de datos: "No permitas que la misma persona (userId) vote el mismo contenido (targetId) más de una vez".
@CompoundIndex(name = "user_target", def = "{'userId': 1, 'targetId': 1}", unique = true)
public class Vote {

    // Esta etiqueta marca que el dato de abajo es la "llave única" o DNI del objeto para que nunca se confunda con otro.
    @Id
    // El "String id" es el casillero donde se guarda ese código único. Se usa texto (String) porque los códigos de MongoDB son una mezcla de letras y números muy larga.
    private String id;

    private String userId;
    private String targetId;
    private String targetType;
    private String voteType;
    private String avatar;
    // Esta etiqueta funciona como un "reloj automático": apenas se crea el voto, anota la fecha y hora exacta sin que nosotros hagamos nada.
    @CreatedDate
    private LocalDateTime createdAt;
}