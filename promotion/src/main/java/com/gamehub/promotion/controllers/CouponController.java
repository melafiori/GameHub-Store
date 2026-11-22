package com.gamehub.promotion.controllers;
import com.gamehub.promotion.services.CouponService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
@RestController public class CouponController {
    private final CouponService coupons;private final String secret; private final com.gamehub.promotion.services.CartPricingService pricing;
    public CouponController(CouponService coupons,@Value("${PROMOTION_SERVICE_SECRET:}") String secret, com.gamehub.promotion.services.CartPricingService pricing){this.coupons=coupons;this.secret=secret;this.pricing=pricing;}
    public record Request(@io.swagger.v3.oas.annotations.media.Schema(description="Codigo de promocion", example="NEON10") String codigo, @io.swagger.v3.oas.annotations.media.Schema(description="Subtotal estimado para la vista previa; Order calcula el subtotal definitivo", example="899990") BigDecimal subtotal) {}
    public record PreviewRequest(String codigo, Map<Long, Integer> items) {}
    @io.swagger.v3.oas.annotations.responses.ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="200",description="Vista previa sin consumo"),@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="400",description="Cupon invalido, vencido o sin usos")})
    @PostMapping("/api/v1/promotions/preview") @Operation(summary="Vista previa de cupon",description="Calcula el descuento estimado sin consumir usos; la orden vuelve a validar el cupon.")
    public ResponseEntity<?> preview(@RequestBody PreviewRequest request){try{return ResponseEntity.ok(coupons.preview(request.codigo(),pricing.subtotal(request.items())));}catch(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}}
    @io.swagger.v3.oas.annotations.responses.ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="200",description="Uso aplicado o recuperado"),@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="400",description="Cupon no disponible"),@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="401",description="Secreto incorrecto")})
    @PutMapping("/internal/coupon-uses/{orderKey}") @Operation(summary="Aplicar cupon a una orden",description="Operacion interna idempotente protegida con X-Service-Secret.")
    public ResponseEntity<?> redeem(@PathVariable String orderKey,@RequestHeader(value="X-Service-Secret",required=false) String supplied,@RequestBody Request request){
        byte[] expected=secret.getBytes(StandardCharsets.UTF_8);if(expected.length<32||supplied==null||!MessageDigest.isEqual(expected,supplied.getBytes(StandardCharsets.UTF_8))) return ResponseEntity.status(401).build();
        try{return ResponseEntity.ok(coupons.redeem(orderKey,request.codigo(),request.subtotal()));}catch(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
    }
}
