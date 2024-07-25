package com.generation.medicostos.controller;

import com.generation.medicostos.dto.MedicamentoDTO;
import com.generation.medicostos.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class

MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @GetMapping("/api/medications/search")
    public List<MedicamentoDTO> searchMedications(
            @RequestParam String query,
            @RequestParam int page,
            @RequestParam int size) {
        return medicamentoService.searchMedications(query, page, size);
    }
}
