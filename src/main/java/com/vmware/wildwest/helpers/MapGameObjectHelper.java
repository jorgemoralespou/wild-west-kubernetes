package com.vmware.wildwest.helpers;

import com.vmware.wildwest.models.GameObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Qualifier("map")
public class MapGameObjectHelper implements GameObjectHelper {

    List<GameObject> objects = new ArrayList<GameObject>();

    public MapGameObjectHelper(){
        objects.add(new GameObject("1", "pod_1", GameObject.TYPE.POD));
        objects.add(new GameObject("2", "svc_1", GameObject.TYPE.SERVICE));
        objects.add(new GameObject("3", "pvc_1", GameObject.TYPE.PVC));
        objects.add(new GameObject("4", "pod_2", GameObject.TYPE.POD));
        objects.add(new GameObject("5", "svc_2", GameObject.TYPE.SERVICE));
        objects.add(new GameObject("6", "pvc_2", GameObject.TYPE.PVC));
        objects.add(new GameObject("7", "pod_3", GameObject.TYPE.POD));
        objects.add(new GameObject("8", "svc_3", GameObject.TYPE.SERVICE));
        objects.add(new GameObject("9", "pvc_3", GameObject.TYPE.PVC));
        objects.add(new GameObject("10", "pod_4", GameObject.TYPE.POD));
        objects.add(new GameObject("11", "svc_4", GameObject.TYPE.SERVICE));
        objects.add(new GameObject("12", "pvc_4", GameObject.TYPE.PVC));
    }

    @Override
    public List<GameObject> getPlatformObjects() {
        return objects;
    }

    @Override
    public GameObject getRandomPlatformObject() {
        return objects.get(new Random(objects.size()).nextInt());
    }

    @Override
    public void deletePlatformObject(String gameID, String objectID, String objectType, String objectName) {
        objects.remove(new GameObject(objectID, objectName, objectType));
    }
}
