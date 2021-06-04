package sk.stuba.fei.uim.jp.processdrivencli.petriNetEnvironment;

public enum Mode {

    MANUAL (1),
    SEMIAUTOMATIC (2);

    private final int number;

    Mode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}