package com.fly.common.core.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.fly.common.core.domain.R;
import com.fly.common.core.domain.TableDataInfo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class BaseController {

    public R<Void> toR(int row){
        return row > 0 ? R.success() : R.failed();
    }

    public R<Void> toR(boolean result){
        return result ? R.success() : R.failed();
    }

    public TableDataInfo getTableDataInfo(List<?> list) {

        // questionVOS == null || questionVOS.isEmpty()
        if (CollectionUtil.isEmpty(list)) {
            return TableDataInfo.empty();
        }

        long total = new PageInfo<>(list).getTotal(); // 获取符合查询条件的数据的总数
        return TableDataInfo.success(list, total);
    }
}
