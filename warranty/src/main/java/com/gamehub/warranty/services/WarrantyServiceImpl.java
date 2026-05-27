package com.gamehub.warranty.services;

import com.gamehub.warranty.exceptions.WarrantyException;
import com.gamehub.warranty.models.Warranty;
import com.gamehub.warranty.repositories.WarrantyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarrantyServiceImpl implements WarrantyService {

    private final WarrantyRepository warrantyRepository;

    @Transactional
    @Override
    public Warranty save(Warranty warranty) {
        if(warranty.getMotivo() == null || warranty.getMotivo().isBlank()) {
            throw new WarrantyException(
                    "Debe ingresar un motivo");
        }

        warranty.setEstado("PENDIENTE");
        warranty.setFechaSolicitud(LocalDateTime.now());

        return warrantyRepository.save(warranty);
    }

    @Transactional
    @Override
    public Warranty updateById(Long id, Warranty warranty) {

        Warranty currentWarranty = this.findById(id);
        currentWarranty.setEstado(warranty.getEstado());
        currentWarranty.setResolution(warranty.getResolution());

        return warrantyRepository.save(currentWarranty);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {

        Warranty warranty = this.findById(id);
        if(warranty.getResolution() == null || warranty.getResolution().isBlank()) {
            throw new WarrantyException("No se puede cerrar una garantía sin resolución");
        }

        warranty.setEstado("CERRADA");
        warrantyRepository.save(warranty);
    }

    @Transactional
    @Override
    public Warranty findById(Long id) {
        return warrantyRepository.findById(id).orElseThrow(
                () -> new WarrantyException("Garantía no encontrada"));
    }

    @Transactional
    @Override
    public List<Warranty> findAll() {
        return warrantyRepository.findAll();
    }

    @Transactional
    @Override
    public List<Warranty> findByUserId(Long userId) {
        return warrantyRepository.findByUserId(userId);
    }

    @Transactional
    @Override
    public List<Warranty> findByProductId(Long productId) {
        return warrantyRepository.findByProductId(productId);
    }

    @Transactional
    @Override
    public List<Warranty> findByEstado(String estado) {
        return warrantyRepository.findByEstado(estado);
    }
}
