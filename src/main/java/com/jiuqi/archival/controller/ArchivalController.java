package com.jiuqi.archival.controller;

import com.jiuqi.archival.dto.ArchivalDoJobDto;
import com.jiuqi.archival.mapper.mysqlsource.GDMapper;
import com.jiuqi.archival.service.ArchivalService;
import com.jiuqi.archival.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rest/archival")
@Api(tags = "归档接口")
public class ArchivalController {

    @Autowired
    GDMapper gdMapper;
    @Autowired
    ArchivalService archivalService;

    @GetMapping("getTableNamss")
    @ApiOperation("获取目标库表名称")
    public Result getTableNamss() throws Exception {
        List<String> tableNames = gdMapper.getTableNames();
        return new Result(tableNames);
    }

    @PostMapping("beginJobByTables")
    @ApiOperation("根据表名归档")
    public Result doBeginJob(@RequestBody ArchivalDoJobDto archivalDoJobDto) throws Exception {
        Result result = archivalService.doArchivalJob(archivalDoJobDto);
        return result;
    }


}

