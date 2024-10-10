package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class BattSocService extends SoaService {
    public static final String SERVICE_NAME = "SN_LANTERNDANCE";

    private static class _Holder {
        private static final BattSocService _instance = new BattSocService();
    }

    private BattSocService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
