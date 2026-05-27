package com.gamehub.notification.models;

import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class Audit {
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAT;

    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    @PrePersist
    public void prePersit() {
        this.createdAT = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();

    }
    @PreUpdate
    public void preUpdate(){
        this.updateAt = LocalDateTime.now();
    }
}
