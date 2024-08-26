package org.apache.dubbo.demo.consumer.router;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author jinghui
 */
public final class OrderClusterIsolationUtil {

    private OrderClusterIsolationUtil() {}


    public static final String SEPARATOR = "#";

    public static final String GENERALIZE_INVOKE_METHOD = "$invoke";

//    public static final String DEPLOY_APP_NAME = "deploy.app.name";
public static final String DEPLOY_APP_NAME = "application";
    /**
     * api接口前缀，不是这个前缀的不需要处理 ORDER-API
     */
    public final static String ORDER_API_ROUTE_API_PATH_PREFIX = "org.apache.dubbo.demo";

    /**
     * api接口前缀，不是这个前缀的不需要处理 ORDER-DATA-API
     */
    public final static String ORDER_DATA_API_ROUTE_API_PATH_PREFIX = "com.shizhuang.duapp.trade.order.data.api";

    /**
     * api接口前缀，不是这个前缀的不需要处理 ORDER-CENTRIC-API
     */
    public final static String ORDER_CENTRIC_API_ROUTE_API_PATH_PREFIX = "com.shizhuang.duapp.trade.order.api";

    /**
     * api接口前缀，不是这个前缀的不需要处理 CENTRIC-OPEN-API
     */
    public final static String CENTRIC_OPEN_API_ROUTE_API_PATH_PREFIX = "com.shizhuang.duapp.trade.order.service";

    /**
     * 应用名 order-interface
     */
//    public final static String ORDER_INTERFACE = "order-interfaces";
    public final static String ORDER_INTERFACE = "demo-provider";
    /**
     * 应用名 order-centric-interface
     */
    public final static String ORDER_CENTRIC_INTERFACE = "order-centric-interface";



    enum ScopeEnum {
        // 全域
        ALL("all", 0),

        // 方法维度
        METHOD("method", 1),

        // 接口维度
        INTERFACES("interfaces", 2),

        // 应用维度
        APPLICATION("application", 3);

        final String scope;

        /**
         * 优先级, 越小越优先
         */
        final int order;

        ScopeEnum(String scope, int order) {
            this.scope = scope;
            this.order = order;
        }

        public String getScope() {
            return scope;
        }

        public int getOrder() {
            return order;
        }

        public static ScopeEnum scopeValueOf(String scope) {
            if (Objects.isNull(scope)) {
                return null;
            }

            for (ScopeEnum item : values()) {
                if (Objects.equals(item.getScope(), scope)) {
                    return item;
                }
            }

            return null;
        }
    }
}

