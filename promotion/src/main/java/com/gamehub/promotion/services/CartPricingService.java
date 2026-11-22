package com.gamehub.promotion.services;
import com.gamehub.promotion.clients.ProductClient;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
@Service
public class CartPricingService {
    private final ProductClient products;
    public CartPricingService(ProductClient products) { this.products = products; }
    public BigDecimal subtotal(Map<Long, Integer> items) {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("El carrito no puede estar vacio");
        BigDecimal result = BigDecimal.ZERO;
        for (var item : items.entrySet()) {
            if (item.getKey() == null || item.getKey() <= 0 || item.getValue() == null || item.getValue() <= 0)
                throw new IllegalArgumentException("Producto o cantidad invalidos");
            var product = products.find(item.getKey());
            if (product == null || !"ACTIVO".equalsIgnoreCase(String.valueOf(product.get("estado"))) || !(product.get("precioVigente") instanceof Number price))
                throw new IllegalArgumentException("Producto no disponible");
            BigDecimal unit = new BigDecimal(price.toString());
            if (unit.signum() <= 0) throw new IllegalArgumentException("Precio invalido");
            result = result.add(unit.multiply(BigDecimal.valueOf(item.getValue())));
        }
        return result;
    }
}