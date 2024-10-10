package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class SeatService extends SoaService {
    public static final String SN_SEAT = "SN_SEAT";

    private static class _Holder {
        private static final SeatService _instance = new SeatService();
    }

    private SeatService() {
        super(SN_SEAT);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
