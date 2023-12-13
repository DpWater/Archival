package com.jiuqi.archival.plugs.reader;

import com.jiuqi.archival.interfaces.DataReader;
import com.jiuqi.archival.mapper.mysqlsource.GDMapper;
import com.jiuqi.archival.utils.SpringContextUtil;
import com.jiuqi.archival.vo.GDColumn;
import com.jiuqi.archival.vo.TableDesc;

import java.util.List;
import java.util.Map;

public class MysqlReader implements DataReader<Map> {

    GDMapper gdMapper;

    public MysqlReader() {
        this.gdMapper = SpringContextUtil.getBean(GDMapper.class);
    }

    @Override
    public List readData(Map map) {
        String tableName = map.get("tableName").toString();
        String startIndex = map.get("startIndex").toString();
        String endIndex = map.get("endIndex").toString();
        String index = map.get("indexName").toString();
        int count = Integer.parseInt(endIndex) - Integer.parseInt(startIndex);
        List<Map> data =gdMapper.selectDataByTableNameAndIndex(tableName,index,startIndex,count+"");
        return data;
    }

    @Override
    public TableDesc getColumn(String tableName) {
        List<GDColumn> columns = gdMapper.getTableDesc(tableName);
        TableDesc desc=new TableDesc();
        desc.setDesc(columns);
        return desc;
    }

}
