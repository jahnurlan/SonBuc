package com.example.adminms.feigns;

import com.example.adminms.model.response.PlanStatisticsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(
        name = "plan-ms",
        url = "${feign.client.plan-service.url}"
)
public interface PlanServiceFeignClient {

    @GetMapping("/plan/admin/info")
    PlanStatisticsResponseDto getUserStatistics();

}

