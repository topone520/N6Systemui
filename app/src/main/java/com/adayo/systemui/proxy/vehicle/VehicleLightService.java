package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class VehicleLightService extends SoaService {
    public static final String SERVICE_NAME = "SN_LIGHT";

    private static class _Holder {
        private static final VehicleLightService _instance = new VehicleLightService();
    }

    private VehicleLightService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
