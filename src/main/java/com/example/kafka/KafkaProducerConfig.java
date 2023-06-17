package com.example.kafka;

import com.example.dto.request.NotificationRequestEvent;
import com.example.dto.request.PasswordRecoveryNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Slf4j
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "user-identity-transactional-id");
        return props;
    }

    // TRANSACTION MANAGER-----------------------------------------------------------------------
    @Bean
    @Primary
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public KafkaTransactionManager<String, NotificationRequestEvent> kafkaTransactionManager(
            final ProducerFactory<String, NotificationRequestEvent> producerFactoryTransactional) {
        return new KafkaTransactionManager<>(producerFactoryTransactional);
    }

    @Bean(name = "kafkaTransactionManagerForPasswordRecoveryNeeds")
    public KafkaTransactionManager<String, PasswordRecoveryNotificationRequest>
    kafkaTransactionManagerForPasswordRecoveryNeeds(
            final ProducerFactory<String, PasswordRecoveryNotificationRequest>
                    producerFactoryTransactional) {
        return new KafkaTransactionManager<>(producerFactoryTransactional);
    }

    // TRANSACTION
    // MANAGER-----------------------------------------------------------------------------------
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(config);
    }

    // PRODUCER
    // FACTORY-----------------------------------------------------------------------------------------------------------
    @Bean
    public ProducerFactory<String, NotificationRequestEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, PasswordRecoveryNotificationRequest>
    producerFactoryForPasswordRecoveryNeeds() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    // KAFKA
    // TEMPLATES-----------------------------------------------------------------------------------------------------------
    @Bean
    public KafkaTemplate<String, NotificationRequestEvent> kafkaTemplate(
            final ProducerFactory<String, NotificationRequestEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, PasswordRecoveryNotificationRequest>
    kafkaTemplateForPasswordRecoveryNeeds(
            final ProducerFactory<String, PasswordRecoveryNotificationRequest> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // KAFKA
    // TOPICS-----------------------------------------------------------------------------------------------------------

    @Bean
    public NewTopic topicForNotificationRequest() {
        return new NewTopic("notification-request", 2, (short) 2);
    }

    @Bean
    public NewTopic topicForPasswordRecoveryRequest() {
        return new NewTopic("password-recovery", 2, (short) 2);
    }

    @Bean
    public NewTopic topicForDeleteCodeRequest() {
        return new NewTopic("delete-code-request", 2, (short) 2);
    }
}
