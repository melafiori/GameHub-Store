package com.gamehub.inventory.services;
import com.gamehub.inventory.models.StockAdjustment;import com.gamehub.inventory.repositories.*;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import org.springframework.web.server.ResponseStatusException;import org.springframework.http.HttpStatus;import java.time.LocalDateTime;
@Service public class StockAdjustmentService {
 private final InventarioRepository inventories;private final StockAdjustmentRepository movements;
 public StockAdjustmentService(InventarioRepository i,StockAdjustmentRepository m){inventories=i;movements=m;}
 @Transactional public StockAdjustment adjust(Long product,Long actor,String key,int delta,String reason){
  if(product==null||product<=0||actor==null||actor<=0||key==null||key.isBlank()||key.length()>128||delta==0||reason==null||reason.isBlank()||reason.trim().length()>500) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Datos invalidos");
  var inv=inventories.lockProduct(product).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto sin inventario"));var old=movements.findByProductIdAndActorIdAndRequestKey(product,actor,key);
  if(old.isPresent()){var m=old.get();if(!m.getDelta().equals(delta)||!m.getMotivo().equals(reason.trim()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Clave ya utilizada con otros datos");return m;}
  int before=inv.getStockDisponible();long after=(long)before+delta;if(after<0||after>Integer.MAX_VALUE)throw new ResponseStatusException(HttpStatus.CONFLICT,"Stock insuficiente o fuera de rango");inv.setStockDisponible((int)after);
  var m=new StockAdjustment();m.setProductId(product);m.setActorId(actor);m.setRequestKey(key);m.setDelta(delta);m.setMotivo(reason.trim());m.setStockAnterior(before);m.setStockPosterior((int)after);m.setFecha(LocalDateTime.now());inventories.save(inv);return movements.saveAndFlush(m);
 }
}
