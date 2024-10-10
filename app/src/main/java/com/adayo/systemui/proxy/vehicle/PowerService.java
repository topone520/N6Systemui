package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class PowerService extends SoaService {
    public static final String SN_SEAT = "SN_VEHICLE_POWER";

    private static class _Holder {
        private static final PowerService _instance = new PowerService();
    }

    private PowerService() {
        super(SN_SEAT);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
