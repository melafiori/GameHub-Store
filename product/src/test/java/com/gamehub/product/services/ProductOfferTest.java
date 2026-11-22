package com.gamehub.product.services;
import com.gamehub.product.models.Product;import org.junit.jupiter.api.Test;import static org.assertj.core.api.Assertions.assertThat;
class ProductOfferTest {
 @Test void fixedSkuOffersAndOrdinaryProduct(){String[] skus={"GH-ORIGINAL-0002","GH-ORIGINAL-0004","GH-ORIGINAL-0019","GH-ORIGINAL-0029","OTHER"};double[] prices={999990,449990,110990,40990,1000},expected={899991,404991,99891,36891,1000};for(int i=0;i<skus.length;i++){Product p=new Product();p.setSku(skus[i]);p.setPrecio(prices[i]);assertThat(p.getPrecioVigente()).isEqualTo(expected[i]);}}
}
