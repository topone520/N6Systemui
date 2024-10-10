package com.adayo.systemui.proxy.vehicle;

import com.adayo.soavb.service.SoaService;

public class VehicleSceneService extends SoaService {
    public static final String SERVICE_NAME = "SN_SCENE_MODE";

    private static class _Holder {
        private static final VehicleSceneService _instance = new VehicleSceneService();
    }
    private VehicleSceneService() {
        super(SERVICE_NAME);
    }

    public static SoaService getInstance() {
        return _Holder._instance;
    }
}
