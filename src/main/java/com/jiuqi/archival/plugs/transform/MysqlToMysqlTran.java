package com.jiuqi.archival.plugs.transform;

import com.jiuqi.archival.interfaces.DataTransform;
import com.jiuqi.archival.vo.TableDesc;

public class MysqlToMysqlTran implements DataTransform {

    @Override
    public TableDesc transFormData(TableDesc desc) {
        return desc;
    }
}
