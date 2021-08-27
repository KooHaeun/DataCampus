package com.example.chopchop;

public class sind_difData {
    Float cos1;
    Float cos2;
    Float sin1;
    Float sin2;
    Float sin_dif;

    public sind_difData(Float cos1, Float cos2, Float sin1, Float sin2, Float sin_dif) {
        this.cos1 = cos1;
        this.cos2 = cos2;
        this.sin1 = sin1;
        this.sin2 = sin2;
        this.sin_dif = sin_dif;
    }

    public Float getCos1() {
        return cos1;
    }

    public void setCos1(Float cos1) {
        this.cos1 = cos1;
    }

    public Float getCos2() {
        return cos2;
    }

    public void setCos2(Float cos2) {
        this.cos2 = cos2;
    }

    public Float getSin1() {
        return sin1;
    }

    public void setSin1(Float sin1) {
        this.sin1 = sin1;
    }

    public Float getSin2() {
        return sin2;
    }

    public void setSin2(Float sin2) {
        this.sin2 = sin2;
    }

    public Float getSin_dif() {
        return sin_dif;
    }

    public void setSin_dif(Float sin_dif) {
        this.sin_dif = sin_dif;
    }
}
