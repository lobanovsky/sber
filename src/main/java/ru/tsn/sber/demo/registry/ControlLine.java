package ru.tsn.sber.demo.registry;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ControlLine {
    private int numberOfLine;
    private BigDecimal sumAmount;
    private BigDecimal sumTransferAmount;
    private BigDecimal sumCommission;
    private String paymentOrderNumber;
    //dd-mm-yyyy
    private String datePaymentOrder;

    public String createCSVLine(String csvSeparator) {
        return String.join(csvSeparator,
                "=" + numberOfLine,
                StringUtils.replace(sumAmount.toString(), ".", ","),
                StringUtils.replace(sumTransferAmount.toString(), ".", ","),
                StringUtils.replace(sumCommission.toString(), ".", ","),
                paymentOrderNumber,
                datePaymentOrder);
    }

}
