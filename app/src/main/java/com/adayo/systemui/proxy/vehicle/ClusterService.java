package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class ClusterService extends SoaService {
    public static final String SERVICE_NAME = "SN_CLUSTER_ADAPTER_SERVICE";

    private static class _Holder {
        private static final ClusterService _instance = new ClusterService();
    }

    private ClusterService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
