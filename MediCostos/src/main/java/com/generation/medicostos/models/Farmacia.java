package com.generation.medicostos.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "farmacias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Farmacia {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String url_img;
    private String url_web;
    @OneToMany(mappedBy = "farmacia")
    private List<Medicamento> medicamentos;
}