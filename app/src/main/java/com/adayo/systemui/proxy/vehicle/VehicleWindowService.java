package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class VehicleWindowService extends SoaService {
    public static final String SN_WINDOW = "SN_WINDOW";

    private static class _Holder {
        private static final VehicleWindowService _instance = new VehicleWindowService();
    }

    private VehicleWindowService() {
        super(SN_WINDOW);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
