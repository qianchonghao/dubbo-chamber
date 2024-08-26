package org.apache.dubbo.demo.consumer.router;


import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 流量等级识别处理器
 */
public class OrderClusterIsolationHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderClusterIsolationHandler.class);

    private static final String GROUP_ID = "trade-order-common";
    private static final String DATA_ID = "cluster-isolation.properties";
    private static final String KEY = "order.cluster.level.config";

    /**
     * ark配置的
     */
    private volatile static OrderRouteConfigInfo orderRouteConfigInfo = new OrderRouteConfigInfo();


    public static OrderRouteConfigInfo getOrderRouteConfigInfo() {
        return orderRouteConfigInfo;
    }

    /**
     * 判断当前的配置router是否打开
     */
    public static boolean getRouterSwitch() {
        return true;
//        OrderRouteConfigInfo configInfo = getOrderRouteConfigInfo();
//        return Objects.nonNull(configInfo) && configInfo.isRouterSwitch();
    }

    /**
     * 判断当前的日志打印是否开启
     */
    public static boolean getRouterLogEnable() {
        return true;
//        OrderRouteConfigInfo configInfo = getOrderRouteConfigInfo();
//        return Objects.nonNull(configInfo) && configInfo.isRouteLogEnable();
    }

    /**
     * 判断当前的默认集群是否打开
     */
    public static boolean getDefaultCluster() {
        OrderRouteConfigInfo configInfo = getOrderRouteConfigInfo();
        return Objects.nonNull(configInfo) && configInfo.isDefaultCluster();
    }

//    private static List<OrderRouteConfigInfo.ConditionInfo> getConditionList() {
//        OrderRouteConfigInfo configInfo = getOrderRouteConfigInfo();
//        if (Objects.nonNull(configInfo) && CollectionUtils.isNotEmpty(configInfo.getConditionInfoList())) {
//            return configInfo.getConditionInfoList();
//        }
//        return new ArrayList<>();
//    }
//
//    public OrderClusterIsolationHandler(Properties properties) {
//
//        // 获取配置中心
//        ArkConfigService configService = ArkConfigFactory.createConfigService(properties);
//
//        // 处理集群级别相关信息
//        handleClusterLevel(configService);
//
//        // 监听配置集处理
//        addConfigListener(configService);
//
//        log.info("OrderClusterIsolationHandler:{}", JsonUtils.serialize(getOrderRouteConfigInfo()));
//    }
//
//
//    private void handleClusterLevel(ArkConfigService configService) {
//        // 拉取配置
//        ValueContent value = configService.getValue(buildGetValueRequest());
//        if (Boolean.TRUE.equals(value.getExists())) {
//
//            renewConfigInfo(value.getValue());
//            if (log.isInfoEnabled()) {
//                log.info("handleClusterLevel, value:{}", JsonUtils.serialize(value));
//            }
//        }
//    }
//
//    private void addConfigListener(ArkConfigService configService) {
//        // 监听配置
//        configService.addListener(DATA_ID, GROUP_ID, new AbstractConfigChangeListener() {
//            @Override
//            public void receiveConfigChange(ConfigChangeEvent configChangeEvent) {
//                ConfigChangeItem changeItem = configChangeEvent.getChangeItem(KEY);
//                if (changeItem != null) {
//
//                    renewConfigInfo(changeItem.getNewValue());
//                    if (log.isInfoEnabled()) {
//                        log.info("addConfigListener, changeItem:{}", JsonUtils.serialize(changeItem));
//                    }
//                }
//            }
//        });
//    }


    /**
     * 更新本地缓存配置
     */
//    public void renewConfigInfo(String json) {
//        try {
//            OrderRouteConfigInfo deserialize;
//            if (StringUtils.isNotBlank(json)) {
//
//                deserialize = JsonUtils.deserialize(json, OrderRouteConfigInfo.class);
//                if (Objects.nonNull(deserialize)) {
//                    List<OrderRouteConfigInfo.ConditionInfo> conditionInfoList = deserialize.getConditionInfoList();
//                    if (CollectionUtils.isNotEmpty(conditionInfoList)) {
//
//                        // 这里直接排好序，省的后面获取的时候再排
//                        List<OrderRouteConfigInfo.ConditionInfo> sortCollect = conditionInfoList.stream()
//                                .sorted()
//                                .collect(Collectors.toList());
//
//                        deserialize.setConditionInfoList(sortCollect);
//                    }
//                }
//            } else {
//                deserialize = new OrderRouteConfigInfo();
//            }
//            orderRouteConfigInfo = deserialize;
//        } catch (Exception e) {
//            if (log.isErrorEnabled()) {
//                log.error("renewMerchantConfigInfo is error, json:{}", json, e);
//            }
//        }
//    }


    /**
     * 获取目标应用
     * <p>
     * 这里需要有总开关
     * 流量百分比灰度（随机数生成）
     * appName + 接口名 + 方法名 维度的切换
     */
    public static String getClusterDestApp(String appName, String interfaceName, String methodName) {
//        List<OrderRouteConfigInfo.ConditionInfo> conditionList = getConditionList();
        List<OrderRouteConfigInfo.ConditionInfo> conditionList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(conditionList)) {
            // @Chamber todo core: 按照比例灰度，命中灰度，则路由至【新增】的APP，没有命中，则路由至【基线】APP
            // [1,10001]
            int random = ThreadLocalRandom.current().nextInt(1, 10001);
            OrderRouteConfigInfo.ConditionInfo conditionInfo = conditionList.stream()
                    .filter(Objects::nonNull)
                    .filter(e -> e.isMatch(appName, interfaceName, methodName, random))
                    .findFirst().orElse(null);
            return Objects.nonNull(conditionInfo) ? conditionInfo.getDestApp() : null;
        }
        return null;
    }


//    private GetValueRequest buildGetValueRequest() {
//        GetValueRequest request = new GetValueRequest();
//        request.setGroupId(GROUP_ID);
//        request.setDataId(DATA_ID);
//        request.setKey(KEY);
//        return request;
//    }
}
