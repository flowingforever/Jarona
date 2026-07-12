package pro.fazeclan.river.jarona.nametag.entity;

import lombok.Getter;

public enum DisplayBillboard {

    FIXED(0),
    VERTICAL(1),
    HORIZONTAL(2),
    CENTER(3);

    @Getter
    private final int value;

    DisplayBillboard(int value) {
        this.value = value;
    }

}
