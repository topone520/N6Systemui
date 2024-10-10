package com.adayo.systemui.proxy.vehicle;


import com.adayo.soavb.service.SoaService;

public class NotifyVoiceService extends SoaService {
    public static final String SN_VOICE = "SN_VOICE";

    private static class _Holder {
        private static final NotifyVoiceService _instance = new NotifyVoiceService();
    }

    private NotifyVoiceService() {
        super(SN_VOICE);
    }

    public static SoaService getInstance() {
        return NotifyVoiceService._Holder._instance;
    }
}
