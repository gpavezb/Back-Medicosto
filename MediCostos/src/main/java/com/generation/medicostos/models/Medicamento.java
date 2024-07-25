package com.generation.medicostos.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "medicamentos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Medicamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "complemento")
    private String complemento;

    @Column(name = "precio")
    private BigDecimal precio;

    @Column(name = "url_imagen")
    private String urlImagen;

    @Column(name = "url_medicamento")
    private String urlMedicamento;

    @ManyToOne
    @JoinColumn(name = "farmacia_id")
    private Farmacia farmacia;
}