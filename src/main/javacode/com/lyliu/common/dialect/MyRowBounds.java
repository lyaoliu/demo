package com.lyliu.common.dialect;

import org.apache.ibatis.session.RowBounds;

/**
 * ${name} class
 *
 * @author lyliu
 * @date 2018/05/05 下午 4:59
 */
public class MyRowBounds extends RowBounds {
    public static final int NO_ROW_OFFSET = 0;
    public static final int NO_ROW_LIMIT = 20;
    private final boolean isCount;
    public MyRowBounds() {
        super(0,20);
        this.isCount = false;

    }

    public MyRowBounds(int offset, int limit,boolean isCount) {
        super(offset,limit);
        this.isCount=isCount;
    }

    public MyRowBounds(boolean isCount) {
        super(0,20);
        this.isCount=isCount;
    }
    public boolean isCount() {
        return isCount;
    }
}
