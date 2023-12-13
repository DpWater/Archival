package com.jiuqi.archival.interfaces;

import com.jiuqi.archival.vo.TableDesc;

import java.util.List;
import java.util.Map;

public interface DataReader<T> {
    List<T> readData(Map map);
    TableDesc getColumn(String tableName);
}
