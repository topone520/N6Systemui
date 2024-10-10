package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class VehicleDoorService extends SoaService {

    public static final String SERVICE_NAME = "SN_DOOR";

    private static class _Holder {
        private static final VehicleDoorService _instance = new VehicleDoorService();
    }

    private VehicleDoorService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
