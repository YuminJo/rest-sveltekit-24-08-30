package com.ll.rsv.global.scope;
import com.ll.rsv.global.scope.transaction.TransactionBeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ScopeConfig {
    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new TransactionBeanFactoryPostProcessor();
    }
}