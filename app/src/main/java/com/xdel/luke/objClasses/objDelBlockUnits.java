package com.xdel.luke.objClasses;

import java.util.List;

public class objDelBlockUnits {
    public String DelBlock;
    public List<String> DelUnits;
    public List<String> DelUnitsCombined;

    public objDelBlockUnits(String aDelBlock, List<String> aDelUnits, List<String> aDelUnitsCombined) {
        this.DelBlock = aDelBlock;
        this.DelUnits = aDelUnits;
        this.DelUnitsCombined = aDelUnitsCombined;
    }
}
