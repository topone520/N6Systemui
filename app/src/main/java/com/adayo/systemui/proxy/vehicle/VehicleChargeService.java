package com.adayo.systemui.proxy.vehicle;

import com.adayo.soavb.service.SoaService;

public class VehicleChargeService extends SoaService {

    public static final String SERVICE_NAME = "SN_CHARGE";

    private static class _Holder {
        private static final VehicleChargeService _instance = new VehicleChargeService();
    }

    private VehicleChargeService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return VehicleChargeService._Holder._instance;
    }
}