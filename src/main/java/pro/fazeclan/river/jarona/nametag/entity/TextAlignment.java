package pro.fazeclan.river.jarona.nametag.entity;

import lombok.Getter;

public enum TextAlignment {

    CENTER(0),
    LEFT(1),
    RIGHT(2);

    @Getter
    private final int value;

    TextAlignment(int value) {
        this.value = value;
    }

}
