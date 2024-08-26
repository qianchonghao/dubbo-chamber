package org.apache.dubbo.demo.consumer.router;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.AbstractRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * order：应用分组路由选择器
 * 这个路由选择只会处理 duapp-order-api 的接口
 * 不影响其他接口的路由
 *
 */
public class OrderRouter extends AbstractRouter {

    private static final Logger log = LoggerFactory.getLogger(OrderRouter.class);

    public OrderRouter(URL url) {
        super(url);
        // router执行顺序，越小越早执行；最先执行
        setPriority(-10000);
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        try {
            return getFilterInvokers(invokers, url, invocation);
        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error("OrderRouter#route is error", e);
            }
        }
        return invokers;
    }

    /**
     * 获取条件过滤之后的 invokers
     * @Chamber todo core:
     * 1. 配置：各上游应用读取【同一份】灰度配置（即DATA_ID,GROUP_ID相同）
     *         {
     *             "scope": "method",
     *             "destApp": "trade-order-core",
     *             "condition": [
     *                 "order-centric-interface#com.shizhuang.duapp.trade.order.data.api.biz.delivery.unit.TradeSubOrderForDeliveryUnitApi#querySubOrderForDelivery"
     *             ],
     *             "percent": 10000
     *         }
     * 2. dubbo URL： 存储 application, interface, method,parameter
     */
    private <T> List<Invoker<T>> getFilterInvokers(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        if (CollectionUtils.isEmpty(invokers)) {
            return invokers;
        }

        // 只处理 duapp-order-api 前缀的接口，其他接口不处理
        String serviceName = url.getServiceInterface();
        if (StringUtils.isBlank(serviceName) || !serviceName.startsWith(OrderClusterIsolationUtil.ORDER_API_ROUTE_API_PATH_PREFIX)) {
            return invokers;
        }

        boolean routerSwitch = OrderClusterIsolationHandler.getRouterSwitch();
        if (!routerSwitch) {
            // 如果配置的这个路由是关闭的，就不执行了
            return invokers;
        }

        // 根据appName、serviceName、methodName，识别流量级别；
        String appName = url.getParameter(CommonConstants.APPLICATION_KEY);
        String methodName = getRealMethodName(invocation);

        // 先取配置的，如果没有则取 Rpc 链路中的参数的
        String destApp = OrderClusterIsolationHandler.getClusterDestApp(appName, serviceName, methodName);

        if (OrderClusterIsolationHandler.getRouterLogEnable()) {
            log.info("getClusterLevelCode, appName:{}, serviceName:{}, methodName:{}, destApp:{}", appName, serviceName, methodName, destApp);
        }

        // @Chamber todo: 没有命中灰度配置，则默认走 基线order-interface
        if (StringUtils.isBlank(destApp)) {
            // 过滤 【非基线】的invoker，只调用 【基线】order-interface的invoker
            return filterBasics(invokers);
        }

        // 否则，过滤出对应的集群列表
        List<Invoker<T>> filterInvokers = filterDestApp(invokers, destApp);

        // 如果指定的应用没找到list，不兜底, 默认不兜底；
        boolean defaultCluster = OrderClusterIsolationHandler.getDefaultCluster();

        // 如果流量集群过滤之后，没有列表；走兜底的集群，就是原始集群这个元数据为空
        if (defaultCluster && CollectionUtils.isEmpty(filterInvokers)) {
            filterInvokers = filterBasics(invokers);
        }

        return filterInvokers;
    }

    public String getRealMethodName(Invocation invocation) {
        String methodName = invocation.getMethodName();
        // 如果是泛化调用，需要处理下真实的方法名称
        if (Objects.equals(methodName, OrderClusterIsolationUtil.GENERALIZE_INVOKE_METHOD)) {
            return getInvokeRealMethodName(invocation);
        }
        return methodName;
    }

    public String getInvokeRealMethodName(Invocation invocation) {
        Object[] arguments = invocation.getArguments();
        if (Objects.nonNull(arguments) && arguments.length > 0) {
            return (String) arguments[0];
        }
        return StringUtils.EMPTY;
    }

    /**
     * 应用级别过滤, 过滤出指定应用
     */
    private <T> List<Invoker<T>> filterDestApp(List<Invoker<T>> invokers, String destApp) {
        return invokers.stream()
                .filter(invoker -> Objects.equals(invoker.getUrl().getParameter(OrderClusterIsolationUtil.DEPLOY_APP_NAME), destApp))
                .collect(Collectors.toList());
    }


    /**
     * 根据invoker应用名筛选出原应用order-interface invoker
     */
    private <T> List<Invoker<T>> filterBasics(List<Invoker<T>> invokers) {
        return invokers.stream()
                .filter(invoker -> OrderClusterIsolationUtil.ORDER_INTERFACE.equals(invoker.getUrl().getParameter(OrderClusterIsolationUtil.DEPLOY_APP_NAME)))
                .collect(Collectors.toList());
    }

}
