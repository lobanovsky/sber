package ru.tsn.sber.demo.registry;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Sum {
    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal transferAmount = BigDecimal.ZERO;
    private BigDecimal commission = BigDecimal.ZERO;

    public void add(Row row) {
        amount = amount.add(row.getAmount());
        transferAmount = transferAmount.add(row.getTransferAmount());
        commission = commission.add(row.getCommission());
    }

}
