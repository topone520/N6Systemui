package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class FragranceService extends SoaService {
    public static final String SN_FRAGRANCE = "SN_FRAGRANCE";

    private static class _Holder {
        private static final FragranceService _instance = new FragranceService();
    }

    private FragranceService() {
        super(SN_FRAGRANCE);
    }

    public static SoaService getInstance() {
        return FragranceService._Holder._instance;
    }
}
