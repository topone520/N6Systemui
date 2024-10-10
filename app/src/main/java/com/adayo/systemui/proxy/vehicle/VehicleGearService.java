package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class VehicleGearService extends SoaService {
    public static final String SERVICE_NAME = "SN_VEHICLE_GEARS";

    private static class _Holder {
        private static final VehicleGearService _instance = new VehicleGearService();
    }

    private VehicleGearService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
