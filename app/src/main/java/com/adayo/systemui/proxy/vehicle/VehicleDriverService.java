package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class VehicleDriverService extends SoaService {

    public static final String SERVICE_NAME = "SN_DRIVER";

    private static class _Holder {
        private static final VehicleDriverService _instance = new VehicleDriverService();
    }

    private VehicleDriverService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
