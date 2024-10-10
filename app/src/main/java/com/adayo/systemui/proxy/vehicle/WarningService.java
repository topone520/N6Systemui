package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class WarningService extends SoaService {
    public static final String SN_WARNING = "SN_WARNING";

    private static class _Holder {
        private static final WarningService _instance = new WarningService();
    }

    private WarningService() {
        super(SN_WARNING);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
