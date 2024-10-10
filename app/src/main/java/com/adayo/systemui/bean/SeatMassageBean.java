package com.adayo.systemui.bean;

public class SeatMassageBean {
    private String mode;
    private boolean moveCheckBox;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isMoveCheckBox() {
        return moveCheckBox;
    }

    public void setMoveCheckBox(boolean moveCheckBox) {
        this.moveCheckBox = moveCheckBox;
    }

    public SeatMassageBean(String mode, boolean moveCheckBox) {
        this.mode = mode;
        this.moveCheckBox = moveCheckBox;
    }
}
