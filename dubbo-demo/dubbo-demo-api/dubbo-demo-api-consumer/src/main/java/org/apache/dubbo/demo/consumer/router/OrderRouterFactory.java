package org.apache.dubbo.demo.consumer.router;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

@Activate
// @Chamber todo: org.apache.dubbo.common.extension.ExtensionLoader.cacheActivateClass 存储@Active注视的factory
public class OrderRouterFactory implements RouterFactory {

    public OrderRouterFactory() {
    }

    @Override
    public Router getRouter(URL url) {
        return new OrderRouter(url);
    }
}
