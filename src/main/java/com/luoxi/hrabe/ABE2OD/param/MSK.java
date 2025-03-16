package com.luoxi.hrabe.ABE2OD.param;

import it.unisa.dia.gas.jpbc.Element;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 主密钥
 * */

@AllArgsConstructor
@NoArgsConstructor
public class MSK {
    private Element galpha; // G1群元素

    public Element getGalpha() {
        return galpha;
    }

    public void setGalpha(Element galpha) {
        this.galpha = galpha;
    }

    public void showMSK(){
        System.out.println("msk(galpha): " + galpha);
    }
}
