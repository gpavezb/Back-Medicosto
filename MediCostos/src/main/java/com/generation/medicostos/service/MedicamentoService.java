package com.generation.medicostos.service;


import com.generation.medicostos.dto.MedicamentoDTO;
import com.generation.medicostos.models.Medicamento;
import java.util.List;

public interface MedicamentoService {
    Medicamento saveMedicamento(MedicamentoDTO medicationDTO);
    List<Medicamento> getAllMedicamento();
    List<MedicamentoDTO> searchMedications(String query, int page, int size); // Nuevo método
}