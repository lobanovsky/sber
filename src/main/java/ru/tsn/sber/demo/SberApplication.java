package ru.tsn.sber.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.tsn.sber.demo.registry.ChannelEnum;
import ru.tsn.sber.demo.registry.ControlLine;
import ru.tsn.sber.demo.registry.Sum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class SberApplication implements CommandLineRunner {

    private static final String DEFAULT_FOLDER = "etc";
    private static final String SHEET_NAME = "733";
    private static final String EXCEL_FILE_NAME = SHEET_NAME + ".xlsx";

    private static final String CSV_SEPARATOR = ";";

    private static final LocalDateTime DEFAULT_PAYMENT_DATE = LocalDateTime.of(2020, 11, 12, 10, 10, 10);
    private static final String DEFAULT_BRANCH_NUMBER = "5278";
    private static final String DEFAULT_CASHIERS_NUMBER = "5278";
    private static final String DEFAULT_EPS = "5278";
    private static final String DEFAULT_FIO = "ФИО";
    private static final String DEFAULT_ADDRESS = "ПР-Д МАРЬИНОЙ РОЩИ 17-Й МОСКВА Д.1";
    private static final String DEFAULT_PAYMENT_ORDER_NUMBER = "558220";

    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern("HH-mm-ss");
    private static final DateTimeFormatter FORMATTER_PERIOD = DateTimeFormatter.ofPattern("MMyy");

    private final Sum sum = new Sum();

    public static void main(String[] args) {
        SpringApplication.run(SberApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final List<ru.tsn.sber.demo.registry.Row> rows =
                read(Paths.get(DEFAULT_FOLDER)
                        .resolve(EXCEL_FILE_NAME)
                        .toFile()
                        .getAbsolutePath(), 1, SHEET_NAME);
        enrichAndSum(rows);
        Files.write(Paths.get(DEFAULT_FOLDER).resolve(fileName(SHEET_NAME)),
                toCsv(rows),
                Charset.forName("WINDOWS-1251"));
    }

    private String fileName(String prefix) {
        DateTimeFormatter formatterFileName = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return prefix + "-" + LocalDateTime.now().format(formatterFileName) + ".txt";
    }

    private List<String> toCsv(List<ru.tsn.sber.demo.registry.Row> rows) {
        List<String> lines = rows.stream()
                .map(r -> r.createCSVLine(CSV_SEPARATOR))
                .collect(Collectors.toList());
        lines.add(ControlLine.builder()
                .numberOfLine(rows.size())
                .sumAmount(sum.getAmount().setScale(2, RoundingMode.HALF_UP))
                .sumTransferAmount(sum.getTransferAmount().setScale(2, RoundingMode.HALF_UP))
                .sumCommission(sum.getCommission().setScale(2, RoundingMode.HALF_UP))
                .paymentOrderNumber(DEFAULT_PAYMENT_ORDER_NUMBER)
                .datePaymentOrder(DEFAULT_PAYMENT_DATE.format(FORMATTER_DATE))
                .build()
                .createCSVLine(CSV_SEPARATOR));
        return lines;
    }

    public List<ru.tsn.sber.demo.registry.Row> read(String fileName, int skipRows, String sheetName) throws IOException {
        final int ACCOUNT = 0;
        final int DATE = 1;
        final int AMOUNT = 6;
        List<ru.tsn.sber.demo.registry.Row> rows = new ArrayList<>();
        File myFile = new File(fileName);
        FileInputStream fis = new FileInputStream(myFile);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet(sheetName);

        int skipCounter = 0;
        BigDecimal sum = BigDecimal.ZERO;
        for (Row row : sheet) {
            if (skipCounter < skipRows) {
                skipCounter++;
                continue;
            }
            log.info("Account [{}]", row.getCell(ACCOUNT));
            final String account = row.getCell(ACCOUNT).getStringCellValue().trim();
            final Date date = row.getCell(DATE).getDateCellValue();
            final LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            final BigDecimal amount = BigDecimal.valueOf(row.getCell(AMOUNT).getNumericCellValue());
            sum = sum.add(amount);
            rows.add(ru.tsn.sber.demo.registry.Row.builder()
                    .account(StringUtils.leftPad(account, 10, "0"))
                    .date(localDateTime.format(FORMATTER_DATE))
                    .amount(amount.setScale(2, RoundingMode.HALF_UP))
                    .build());
        }
        log.info("Read [{}], rows [{}], sum[{}]", fileName, rows.size(), sum);
        return rows;
    }

    private void enrichAndSum(List<ru.tsn.sber.demo.registry.Row> rows) {
        for (ru.tsn.sber.demo.registry.Row row : rows) {
            row.setTime(DEFAULT_PAYMENT_DATE.format(FORMATTER_TIME));
            row.setBranchNumber(DEFAULT_BRANCH_NUMBER);
            row.setCashiersNumber(DEFAULT_CASHIERS_NUMBER);
            row.setEps(DEFAULT_EPS);
            row.setFio(DEFAULT_FIO);
            row.setAddress(DEFAULT_ADDRESS);
            row.setPeriod(DEFAULT_PAYMENT_DATE.format(FORMATTER_PERIOD));
            row.setTransferAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            row.setCommission(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            row.setChannel(ChannelEnum.ONLINE);

            sum.add(row);
        }
        log.info(sum.toString());
    }

}
