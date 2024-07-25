package com.generation.medicostos.service.implementation;

import com.generation.medicostos.dto.MedicamentoDTO;
import com.generation.medicostos.models.Farmacia;
import com.generation.medicostos.models.Medicamento;
import com.generation.medicostos.repository.FarmaciaRepository;
import com.generation.medicostos.repository.MedicamentoRepository;
import com.generation.medicostos.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicamentoServiceImplementation implements MedicamentoService {
    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private FarmaciaRepository farmaciaRepository;

    @Override
    public Medicamento saveMedicamento(MedicamentoDTO medicamentoDTO) {

        // Buscar farmacia por id
        Long farmaciaId = medicamentoDTO.getFarmaciaId();
        Optional<Farmacia> optionalPharmacy = farmaciaRepository.findById(farmaciaId);

        if (!optionalPharmacy.isPresent()) {
            throw new IllegalArgumentException("Farmacia con id " + farmaciaId + " no encontrada.");
        }

        Farmacia farmacia = optionalPharmacy.get();

        Medicamento medicamento = new Medicamento();
        medicamento.setId(medicamentoDTO.getId());
        medicamento.setNombre(medicamentoDTO.getNombre());
        medicamento.setComplemento(medicamentoDTO.getComplemento());
        medicamento.setPrecio(medicamentoDTO.getPrecio());
        medicamento.setUrlImagen(medicamentoDTO.getUrlImagen());
        medicamento.setUrlMedicamento(medicamentoDTO.getUrlMedicamento());
        medicamento.setFarmacia(farmacia);

        return medicamentoRepository.save(medicamento);
    }

    @Override
    public List<Medicamento> getAllMedicamento() {
        return medicamentoRepository.findAll();
    }

    @Override
    public List<MedicamentoDTO> searchMedications(String query, int page, int size) {
        Page<Medicamento> medicationsPage = medicamentoRepository.findMedicamentosByNombreContainingIgnoreCaseOrComplementoContainingIgnoreCaseAndPrecioGreaterThanOrderByPrecioAsc(query, query, PageRequest.of(page, size));

        return medicationsPage.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private MedicamentoDTO convertToDTO(Medicamento medicamento) {
        MedicamentoDTO dto = new MedicamentoDTO();
        dto.setId(medicamento.getId());
        dto.setNombre(medicamento.getNombre());
        dto.setComplemento(medicamento.getComplemento());
        dto.setPrecio(medicamento.getPrecio());
        dto.setUrlImagen(medicamento.getUrlImagen());
        dto.setUrlMedicamento(medicamento.getUrlMedicamento());
        dto.setFarmaciaId(medicamento.getFarmacia().getId());
        dto.setFarmaciaNombre(medicamento.getFarmacia().getNombre());
        dto.setFarmaciaDireccion(medicamento.getFarmacia().getDireccion());
        dto.setFarmaciaTelefono(medicamento.getFarmacia().getTelefono());
        dto.setFarmaciaUrlImg(medicamento.getFarmacia().getUrl_img());
        dto.setFarmaciaUrlWeb(medicamento.getFarmacia().getUrl_web());
        return dto;
    }
}
