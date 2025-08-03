package com.Agri.AgriBack.Config;

import com.Agri.AgriBack.DTO.SensorDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.catalina.filters.CorsFilter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class corsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // autorise toutes les routes
                        .allowedOrigins("http://localhost:4200") // autorise le frontend Angular
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS","*")
                        .allowedHeaders("*");
            }
        };

    }

    // Kafka ConsumerFactory pour recevoir des chaînes simples
    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory() { //déclares un bean nommé stringConsumerFactory qui renvoie un objet ConsumerFactory.
        Map<String, Object> props = new HashMap<>();  //props est une map contenant toutes les propriétés nécessaires pour Kafka
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //l’adresse du broker Kafka auquel se connecter
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "Agri-group"); //tous les consommateurs dans ce groupe partageront les messages du même topic.
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // va convertir les bytes Kafka → String
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props); //servira à créer les consumers Kafka avec ces propriétés.
    }

    // Kafka Listener Factory utilisant la ConsumerFactory ci-dessus
    //KafkaListenerContainerFactory pour gérer les @KafkaListener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>(); //va gérer des threads de consumers pour écouter Kafka.
        factory.setConsumerFactory(stringConsumerFactory()); //les listeners utiliseront la config stringConsumerFactory pour écouter les chaînes simples.
        return factory;
    }


}
