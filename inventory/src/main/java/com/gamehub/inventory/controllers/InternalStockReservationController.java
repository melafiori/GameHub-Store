package com.gamehub.inventory.controllers;

import com.gamehub.inventory.services.StockReservationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@RestController
@RequestMapping("/internal/stock-reservations")
public class InternalStockReservationController {
    private final StockReservationService service;
    private final String secret;

    public InternalStockReservationController(StockReservationService service,
            @Value("${INVENTORY_SERVICE_SECRET:}") String secret) {
        this.service = service; this.secret = secret;
    }

    private void authorize(String supplied) {
        byte[] expected = secret.getBytes(StandardCharsets.UTF_8);
        if (expected.length < 32 || supplied == null ||
                !MessageDigest.isEqual(expected, supplied.getBytes(StandardCharsets.UTF_8)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/{orderKey}")
    public Map<String, String> reserve(@PathVariable String orderKey,
            @RequestHeader(value = "X-Service-Secret", required = false) String supplied,
            @RequestBody Map<Long, Integer> items) {
        authorize(supplied);
        try { return Map.of("estado", service.reservar(orderKey, items)); }
        catch (IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()); }
        catch (IllegalStateException e) { throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage()); }
    }

    @PutMapping("/{orderKey}/return")
    public Map<String, String> returnStock(@PathVariable String orderKey,
            @RequestHeader(value = "X-Service-Secret", required = false) String supplied) {
        authorize(supplied);
        return Map.of("estado", service.devolver(orderKey));
    }
}
