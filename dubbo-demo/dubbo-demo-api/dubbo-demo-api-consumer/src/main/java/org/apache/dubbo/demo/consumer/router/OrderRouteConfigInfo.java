package org.apache.dubbo.demo.consumer.router;


import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;


public class OrderRouteConfigInfo {

    /**
     * 路由开关
     * true为打开
     */
    private boolean routerSwitch;

    /**
     * 兜底默认集群
     */
    private boolean defaultCluster;

    /**
     * 日志是否打印
     */
    private boolean routeLogEnable;

    /**
     * 匹配条件
     */
    private List<ConditionInfo> conditionInfoList;




    public static class ConditionInfo implements Comparable<ConditionInfo>{
        /**
         * @see OrderClusterIsolationUtil.ScopeEnum
         */
        private String scope;


        /**
         */
        private String destApp;

        /**
         * 条件
         */
        private Set<String> condition;


        /**
         * 流量百分比 [0, 10000]
         */
        private int percent;


        /**
         * 几个维度的配置，有优先级
         * 命中批量了就直接返回
         *
         */
        public boolean isMatch(String appName, String interfaceName, String methodName, int random) {
            String separator = OrderClusterIsolationUtil.SEPARATOR;

            if (Objects.equals(OrderClusterIsolationUtil.ScopeEnum.ALL.getScope(), getScope())) {
                return getPercent() >= random;

            } else if (Objects.equals(OrderClusterIsolationUtil.ScopeEnum.METHOD.getScope(), getScope())) {
                String spliceMethodName = appName + separator + interfaceName + separator + methodName;
                return getPercent() >= random &&
                        CollectionUtils.isNotEmpty(getCondition()) && getCondition().contains(spliceMethodName);

            } else if (Objects.equals(OrderClusterIsolationUtil.ScopeEnum.INTERFACES.getScope(), getScope())) {
                String spliceInterfacesName = appName + separator + interfaceName;
                return getPercent() >= random &&
                        CollectionUtils.isNotEmpty(getCondition()) && getCondition().contains(spliceInterfacesName);

            } else if (Objects.equals(OrderClusterIsolationUtil.ScopeEnum.APPLICATION.getScope(), getScope())) {
                return getPercent() >= random  &&
                        CollectionUtils.isNotEmpty(getCondition()) && getCondition().contains(appName);

            } else {
                return false;
            }
        }

        @Override
        public int compareTo(ConditionInfo o) {
            OrderClusterIsolationUtil.ScopeEnum scopeEnum = OrderClusterIsolationUtil.ScopeEnum.scopeValueOf(getScope());
            OrderClusterIsolationUtil.ScopeEnum otherEnum = OrderClusterIsolationUtil.ScopeEnum.scopeValueOf(o.getScope());

            return Integer.compare((Objects.nonNull(scopeEnum) ? scopeEnum.getOrder() : 0), (Objects.nonNull(otherEnum) ? otherEnum.getOrder() : 0));
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getDestApp() {
            return destApp;
        }

        public void setDestApp(String destApp) {
            this.destApp = destApp;
        }

        public Set<String> getCondition() {
            return condition;
        }

        public void setCondition(Set<String> condition) {
            this.condition = condition;
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }
    }

    public boolean isRouterSwitch() {
        return routerSwitch;
    }

    public void setRouterSwitch(boolean routerSwitch) {
        this.routerSwitch = routerSwitch;
    }

    public boolean isDefaultCluster() {
        return defaultCluster;
    }

    public void setDefaultCluster(boolean defaultCluster) {
        this.defaultCluster = defaultCluster;
    }

    public boolean isRouteLogEnable() {
        return routeLogEnable;
    }

    public void setRouteLogEnable(boolean routeLogEnable) {
        this.routeLogEnable = routeLogEnable;
    }

    public List<ConditionInfo> getConditionInfoList() {
        return conditionInfoList;
    }

    public void setConditionInfoList(List<ConditionInfo> conditionInfoList) {
        this.conditionInfoList = conditionInfoList;
    }
}