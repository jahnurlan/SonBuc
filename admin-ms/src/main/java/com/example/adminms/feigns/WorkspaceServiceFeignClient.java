package com.example.adminms.feigns;

import com.example.adminms.model.response.WorkspaceStatisticsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(
        name = "workspace-ms",
        url = "${feign.client.workspace-service.url}"
)
public interface WorkspaceServiceFeignClient {

    @GetMapping("/ws/admin/info")
    WorkspaceStatisticsResponseDto getUserStatistics();
}

