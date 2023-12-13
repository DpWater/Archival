package com.jiuqi.archival.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class ArchivalDoJobDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ArchivalTask> tableNames;

    public List<ArchivalTask> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<ArchivalTask> tableNames) {
        this.tableNames = tableNames;
    }

    public static class ArchivalTask {
        private String tableName;
        private String index;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }
    }
}
