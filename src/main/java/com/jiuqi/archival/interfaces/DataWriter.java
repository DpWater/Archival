package com.jiuqi.archival.interfaces;

import com.jiuqi.archival.vo.TableDesc;

import java.util.List;
import java.util.Map;

public interface DataWriter<T> {
    void writeData(Map map, List<T> data, TableDesc desc);

    void checkAndInitTable(String tableName, TableDesc tableDasc);
}
