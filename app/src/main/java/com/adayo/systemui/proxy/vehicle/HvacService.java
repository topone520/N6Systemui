package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class HvacService extends SoaService {
    public static final String SN_HVAC = "SN_HVAC";

    private static class _Holder {
        private static final HvacService _instance = new HvacService();
    }

    private HvacService() {
        super(SN_HVAC);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
