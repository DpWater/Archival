package com.jiuqi.archival.MQCustomer;

import com.jiuqi.archival.constdata.MQConstants;
import com.jiuqi.archival.plugs.reader.MysqlReader;
import com.jiuqi.archival.plugs.transform.MysqlToMysqlTran;
import com.jiuqi.archival.plugs.writer.MysqlWriter;
import com.jiuqi.archival.process.TaskProcess;
import com.jiuqi.archival.utils.MapperJson;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkCustomer {

    @RabbitListener(queuesToDeclare = @Queue(MQConstants.QUEUE_GD))
    public void receive1(String message) {
        Map<String, Object> data = MapperJson.jsonToMap(message);

        int startIndex = Integer.parseInt(data.get("startIndex").toString());
        int endIndex = Integer.parseInt(data.get("endIndex").toString());
        int range = endIndex - startIndex + 1;
        int groupSize = range / 10;
        int currentStartIndex = startIndex;

        while (currentStartIndex <= endIndex) {
            int currentEndIndex = currentStartIndex + groupSize - 1;
            data.put("startIndex", String.valueOf(currentStartIndex));
            data.put("endIndex", String.valueOf(currentEndIndex));
            TaskProcess.doTaskItem(new MysqlReader(), new MysqlWriter(), new MysqlToMysqlTran(), data);
            // 更新 currentStartIndex 为下一个组的开始索引
            currentStartIndex = currentEndIndex + 1;
        }


}

//    @RabbitListene r(queuesToDeclare = @Queue(MQConstants.QUEUE_GDrdm))
//    public void receive2(String message){
//        System.out.println("work message2 = " + message);
//    }
}
