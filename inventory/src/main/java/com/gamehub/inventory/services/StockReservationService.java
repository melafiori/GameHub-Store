package com.gamehub.inventory.services;

import com.gamehub.inventory.models.StockReservation;
import com.gamehub.inventory.repositories.InventarioRepository;
import com.gamehub.inventory.repositories.StockReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.TreeMap;

@Service
public class StockReservationService {
    private final StockReservationRepository reservations;
    private final InventarioRepository inventory;
    private final StockAtomicoService stock;
    public StockReservationService(StockReservationRepository reservations, InventarioRepository inventory, StockAtomicoService stock) {
        this.reservations = reservations; this.inventory = inventory; this.stock = stock;
    }

    @Transactional
    public String reservar(String orderKey, Map<Long, Integer> items) {
        if (orderKey == null || orderKey.isBlank() || items == null || items.isEmpty() ||
                items.entrySet().stream().anyMatch(e -> e.getKey() == null || e.getKey() <= 0 || e.getValue() == null || e.getValue() <= 0))
            throw new IllegalArgumentException("Orden y cantidades deben ser válidas");
        Map<Long, Integer> sorted = new TreeMap<>(items);
        var existing = reservations.findForUpdate(orderKey);
        if (existing.isPresent()) {
            if (!existing.get().getItems().equals(sorted)) throw new IllegalStateException("La orden ya tiene otros productos");
            return existing.get().getEstado();
        }
        StockReservation reservation = new StockReservation();
        reservation.setOrderKey(orderKey); reservation.setEstado("RESERVADO"); reservation.setItems(sorted);
        reservations.saveAndFlush(reservation);
        for (var entry : sorted.entrySet()) {
            if (!stock.descontar(entry.getKey(), entry.getValue())) throw new IllegalStateException("Stock insuficiente para producto " + entry.getKey());
        }
        return "RESERVADO";
    }

    @Transactional
    public String devolver(String orderKey) {
        StockReservation reservation = reservations.findForUpdate(orderKey).orElseThrow();
        if ("DEVUELTO".equals(reservation.getEstado())) return "DEVUELTO";
        for (var entry : new TreeMap<>(reservation.getItems()).entrySet()) {
            if (inventory.devolver(entry.getKey(), entry.getValue()) != 1) throw new IllegalStateException("Inventario inconsistente");
        }
        reservation.setEstado("DEVUELTO");
        reservations.save(reservation);
        return "DEVUELTO";
    }
}
