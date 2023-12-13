package com.jiuqi.archival.vo;

import java.io.Serializable;
import java.util.List;

public class TableDesc implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<GDColumn> desc;

    public List<GDColumn> getDesc() {
        return desc;
    }

    public void setDesc(List<GDColumn> desc) {
        this.desc = desc;
    }
}
