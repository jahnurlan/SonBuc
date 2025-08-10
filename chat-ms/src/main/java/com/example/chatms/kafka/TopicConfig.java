package com.example.chatms.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic ninthTopic(){
        return TopicBuilder.name("save-edit-message-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tenthTopic(){
        return TopicBuilder.name("accept-edit-message-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
