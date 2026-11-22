package com.gamehub.order.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Embeddable
public class ShippingAddress {
    @NotBlank @Size(max=100) @Column(name="shipping_region",length=100) private String region;
    @NotBlank @Size(max=100) @Column(name="shipping_comuna",length=100) private String comuna;
    @NotBlank @Size(max=255) @Column(name="shipping_direccion",length=255) private String direccion;
    public ShippingAddress() {}
    public ShippingAddress(String region,String comuna,String direccion){this.region=region;this.comuna=comuna;this.direccion=direccion;}
    public String getRegion(){return region;} public void setRegion(String region){this.region=region;}
    public String getComuna(){return comuna;} public void setComuna(String comuna){this.comuna=comuna;}
    public String getDireccion(){return direccion;} public void setDireccion(String direccion){this.direccion=direccion;}
    public ShippingAddress normalized(){
        if(region==null||comuna==null||direccion==null||region.isBlank()||comuna.isBlank()||direccion.isBlank()||region.length()>100||comuna.length()>100||direccion.length()>255) throw new IllegalArgumentException("Direccion de despacho invalida");
        return new ShippingAddress(region.trim(),comuna.trim(),direccion.trim());
    }
    public String fingerprintPart(){return region.length()+":"+region+comuna.length()+":"+comuna+direccion.length()+":"+direccion;}
}
