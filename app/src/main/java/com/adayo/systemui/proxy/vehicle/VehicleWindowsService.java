package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class VehicleWindowsService extends SoaService {
    public static final String SERVICE_NAME = "SN_WINDOW";

    private static class _Holder {
        private static final VehicleWindowsService _instance = new VehicleWindowsService();
    }

    private VehicleWindowsService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
