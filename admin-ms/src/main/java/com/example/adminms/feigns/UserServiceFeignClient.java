package com.example.adminms.feigns;

import com.example.adminms.model.response.UserStatisticsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(
        name = "user-ms",
        url = "${feign.client.user-service.url}"
)
public interface UserServiceFeignClient {

    @GetMapping("/user/admin/info")
    UserStatisticsResponseDto getUserStatistics();

}

