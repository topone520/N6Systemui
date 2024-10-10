package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class VehiclePowerService extends SoaService {
    public static final String SERVICE_NAME = "SN_VEHICLE_POWER";

    private static class _Holder {
        private static final VehiclePowerService _instance = new VehiclePowerService();
    }

    private VehiclePowerService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
