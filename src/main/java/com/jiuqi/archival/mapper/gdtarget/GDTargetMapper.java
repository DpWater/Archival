package com.jiuqi.archival.mapper.gdtarget;

import com.jiuqi.archival.vo.TableDesc;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GDTargetMapper {


    void createTable(@Param("tableName")String tableName,@Param("tableDesc") TableDesc tableDasc);

    void insertDataByTableName(@Param("tableName")String tableName, @Param("list") List<Map<String, String>> datause);

    void insertDataByTableNameByString(@Param("tableName")String tableName, @Param("columnClaus") String columnClaus,@Param("valuesClause") String valuesClause,@Param("list") List<Map<String, String>> datause);

}
