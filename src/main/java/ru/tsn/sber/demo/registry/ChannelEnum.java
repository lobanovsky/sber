package ru.tsn.sber.demo.registry;

public enum ChannelEnum {
    CASH(1, "Касса банка наличные"),
    CARD(2, "Касса банка карта"),
    TERMINAL_CASH(3, "Терминал Сбербанка наличные"),
    TERMINAL_CARD(4, "Терминал Сбербанка карта"),
    ONLINE(5, "Сбербанк Онлайн, Мобильный банк"),
    AUTO_PAY(6, "Автоплатеж");

    private int n;
    private String description;

    ChannelEnum(int n, String description) {
        this.n = n;
        this.description = description;
    }

    public int getN() {
        return n;
    }

    public String getDescription() {
        return description;
    }
}
