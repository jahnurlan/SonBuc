package com.example.adminms.feigns;

import com.example.adminms.model.response.TimeNoteStatisticsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(
        name = "timer-ms",
        url = "${feign.client.timer-service.url}"
)
public interface TimerServiceFeignClient {

    @GetMapping("/time/admin/info")
    TimeNoteStatisticsResponseDto getUserStatistics();

}

