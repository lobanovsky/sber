package ru.tsn.sber.demo.registry;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Row {
    //dd-mm-yyyy
    private String date;
    //hh-mm-ss
    private String time;
    private String branchNumber;
    private String cashiersNumber;
    private String eps;
    private String account;
    private String fio;
    private String address;
    //mmYY
    private String period;
    private BigDecimal amount;
    private BigDecimal transferAmount;
    private BigDecimal commission;
    private ChannelEnum channel;

    public String createCSVLine(String csvSeparator) {
        return String.join(csvSeparator,
                date,
                time,
                branchNumber,
                cashiersNumber,
                eps,
                account,
                fio,
                address,
                period,
                StringUtils.replace(amount.toString(), ".", ","),
                StringUtils.replace(transferAmount.toString(), ".", ","),
                StringUtils.replace(commission.toString(), ".", ","),
                String.valueOf(channel.getN()));
    }
}
