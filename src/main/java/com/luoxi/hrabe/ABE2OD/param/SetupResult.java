package com.luoxi.hrabe.ABE2OD.param;

public class SetupResult {
    private final PK pk;
    private final MSK msk;

    public SetupResult(PK pk, MSK msk) {
        this.pk = pk;
        this.msk = msk;
    }

    public PK getPk() {
        return pk;
    }

    public MSK getMsk() {
        return msk;
    }
}

