package com.example.planms.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {
    @Bean
    public NewTopic fifthTopic(){
        return TopicBuilder.name("plan-status-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sixthTopic(){
        return TopicBuilder.name("updateInvite-readingStatus-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic seventhTopic(){
        return TopicBuilder.name("create-sharedPlan-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic eighthTopic(){
        return TopicBuilder.name("sh-plan-status-topic")
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

    @Bean
    public NewTopic eleventhTopic(){
        return TopicBuilder.name("planItem-connect-goalPlanItem")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic twelveTopic(){
        return TopicBuilder.name("planItem-disconnect-goalPlanItem")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic firstTopic(){
        return TopicBuilder.name("timer-note-connect-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic secondTopic(){
        return TopicBuilder.name("plan-item-text-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic thirdTopic(){
        return TopicBuilder.name("plan-container-name-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sevenTopic(){
        return TopicBuilder.name("delete-plan-connection-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
