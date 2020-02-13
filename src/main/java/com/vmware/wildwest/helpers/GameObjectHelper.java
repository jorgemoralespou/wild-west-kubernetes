package com.vmware.wildwest.helpers;

import com.vmware.wildwest.models.GameObject;

import java.util.List;

public interface GameObjectHelper {
    List<GameObject> getPlatformObjects();

    GameObject getRandomPlatformObject();

    void deletePlatformObject(String gameID, String objectID, String objectType, String objectName);
}
