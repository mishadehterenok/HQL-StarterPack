package com.example.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "payments")
@ToString(callSuper = true)
@NoArgsConstructor
public class Payment extends IdentifiableEntity {

    public Payment(BigDecimal amount, Employee receiver) {
        this.amount = amount;
        this.receiver = receiver;
    }

    @Column(name = "amount")
    @Getter
    @Setter
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    @Getter
    @Setter
    private Employee receiver;
}
