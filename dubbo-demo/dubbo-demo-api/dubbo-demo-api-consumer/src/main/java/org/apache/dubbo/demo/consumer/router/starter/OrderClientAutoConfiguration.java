package org.apache.dubbo.demo.consumer.router.starter;
//
//import com.alibaba.cloud.nacos.NacosConfigProperties;
//import com.shizhuang.duapp.order.cluster.router.OrderClusterIsolationHandler;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.Resource;
//
///**
// * bean加载读取ark值加载在内存中
// */
//@Configuration
public class OrderClientAutoConfiguration {
//
//    @Resource
//    private NacosConfigProperties nacosConfigProperties;
//
//    @Bean
//    @ConditionalOnMissingBean(OrderClusterIsolationHandler.class)
//    public OrderClusterIsolationHandler orderClusterIsolationHandler() {
//        return new OrderClusterIsolationHandler(nacosConfigProperties.assembleConfigServiceProperties());
//    }
}
