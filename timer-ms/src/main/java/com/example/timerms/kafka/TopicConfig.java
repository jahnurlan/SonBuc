package com.example.timerms.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {
    @Bean
    public NewTopic thirdTopic(){
        return TopicBuilder.name("timer-note-text-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic fifthTopic(){
        return TopicBuilder.name("timer-note-connect-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sixthTopic(){
        return TopicBuilder.name("delete-plan-connection-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
