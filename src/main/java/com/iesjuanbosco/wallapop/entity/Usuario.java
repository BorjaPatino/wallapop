package com.iesjuanbosco.wallapop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El email es obligatorio")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 500)
    @Length(min = 4)
    private String password;

    private String nombre;

    private String telefono; // Nuevo campo
    private String poblacion; // Nuevo campo

    private String rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anuncio> anuncios = new ArrayList<>(); // Relación con sus anuncios
}
