package com.fly.common.core.controller;

import com.fly.common.core.domain.R;

public class BashController {

    public R<Void> toR(int row){
        return row > 0 ? R.success() : R.failed();
    }

    public R<Void> toR(boolean result){
        return result ? R.success() : R.failed();
    }
}
