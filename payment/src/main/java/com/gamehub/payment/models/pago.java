package com.gamehub.payment.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "pagos")

public class pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ordenId;
    private Double monto;
    private String metodo;
    private String estado;

    @Column(unique = true)
    private String codigoTransaccion;

    private LocalDateTime fecha;

    public pago (){
    }
    public  pago (Long ordenId,Double monto, String metodo,String estado,String codigoTransaccion,LocalDateTime fecha){
        this.ordenId = ordenId;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
        this.codigoTransaccion = codigoTransaccion;
        this.fecha = fecha;
    }


}