package com.jiuqi.archival.mapper.mysqlsource;

import com.jiuqi.archival.vo.GDColumn;
import com.jiuqi.archival.vo.TableDesc;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GDMapper {

    List doSql(@Param("sql") String sql);

    List<String> getTableNames();

    //根据表名获取总条数
    int getTotalNum(@Param("tableName") String tableName);

    List<Map> selectDataByTableNameAndIndex(@Param("tableName")String tableName,@Param("index") String index,@Param("startIndex") String startIndex,@Param("endIndex") String endIndex);

    void insertDataByTableName(@Param("tableName")String tableName, List<Map<String, String>> data);

    List<GDColumn> getTableDesc(String tableName);

    int isTableExist(@Param("tableName") String tableName);
}
