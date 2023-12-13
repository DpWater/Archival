package com.jiuqi.archival.service;

import com.jiuqi.archival.dto.ArchivalDoJobDto;
import com.jiuqi.archival.vo.Result;
import org.springframework.stereotype.Service;

public interface ArchivalService {


    Result doArchivalJob(ArchivalDoJobDto archivalDoJobDto);
}
