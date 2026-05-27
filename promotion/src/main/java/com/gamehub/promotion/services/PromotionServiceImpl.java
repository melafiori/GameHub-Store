package com.gamehub.promotion.services;

import com.gamehub.promotion.exceptions.PromotionException;
import com.gamehub.promotion.models.Promotion;
import com.gamehub.promotion.repositories.PromotionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService{

    private final PromotionRepository promotionRepository;

    @Transactional
    @Override
    public Promotion save(Promotion promotion) {
        if(promotionRepository.findByCode(promotion.getCode()).isPresent()) {
            throw new PromotionException("El código ya existe");
        }

        promotion.setEstado("ACTIVA");
        promotion.setUsosActuales(0);

        return promotionRepository.save(promotion);
    }

    @Transactional
    @Override
    public Promotion updateById(Long id, Promotion promotion) {

        Promotion currentPromotion = this.findById(id);

        currentPromotion.setTipo(promotion.getTipo());
        currentPromotion.setValor(promotion.getValor());
        currentPromotion.setFechaInicio(promotion.getFechaInicio());
        currentPromotion.setFechaFin(promotion.getFechaFin());
        currentPromotion.setMontoMinimo(promotion.getMontoMinimo());
        currentPromotion.setUsosMaximos(promotion.getUsosMaximos());
        currentPromotion.setEstado(promotion.getEstado());

        return promotionRepository.save(currentPromotion);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {

        Promotion promotion = this.findById(id);

        promotion.setEstado("INACTIVA");

        promotionRepository.save(promotion);
    }

    @Transactional
    @Override
    public Promotion findById(Long id) {

        return promotionRepository.findById(id).orElseThrow(
                () -> new PromotionException("Promoción no encontrada"));
    }

    @Transactional
    @Override
    public List<Promotion> findAll() {
        return promotionRepository.findAll();
    }

    @Transactional
    @Override
    public Promotion findByCode(String code) {

        return promotionRepository.findByCode(code).orElseThrow(
                () -> new PromotionException("Código no encontrado"));
    }

    @Transactional
    @Override
    public List<Promotion> findByEstado(String estado) {
        return promotionRepository.findByEstado(estado);
    }
}
