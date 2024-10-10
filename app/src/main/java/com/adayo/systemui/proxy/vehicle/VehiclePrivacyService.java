package com.adayo.systemui.proxy.vehicle;

import com.adayo.soavb.service.SoaService;

public class VehiclePrivacyService extends SoaService {
    public static final String SERVICE_NAME = "SN_PRIVACY";

    private static class _Holder {
        private static final VehiclePrivacyService _instance = new VehiclePrivacyService();
    }
    private VehiclePrivacyService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return VehiclePrivacyService._Holder._instance;
    }
}
