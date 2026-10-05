package com.necro.sos.encounters.common.util;

import com.necro.sos.encounters.common.api.SOSManager;

public interface ISOSCaller {
    SOSManager sos_getSOSManager();
    void sos_setSOSManager(SOSManager manager);
    void sos_initSOSManager();

    boolean sos_isSOSSpawn();
    void sos_setSOSSpawn();
}
