package com.vmware.wildwest.helpers;

import com.vmware.wildwest.models.GameObject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.ClientBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Qualifier("kubernetes")
public class KubernetesGameObjectHelper implements GameObjectHelper {

	@Value("${NAMESPACE:wildwest}")
	private String namespace;

	private ApiClient client;

	private CoreV1Api api;

	public KubernetesGameObjectHelper() {
		try {
			// Let's establish a connection to the API server
			client = ClientBuilder.cluster().build();
			// set the global default api-client to the in-cluster one from above
			Configuration.setDefaultApiClient(client);
			// the CoreV1Api loads default api-client from global configuration.
			api = new CoreV1Api();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<GameObject> getPlatformObjects() {
		List<GameObject> gameObjects = new ArrayList<GameObject>();

		gameObjects.addAll(this.getPods());
		gameObjects.addAll(this.getPVs());
		gameObjects.addAll(this.getServices());

		return gameObjects;
	}

	@Override
	public GameObject getRandomPlatformObject() {
		List<GameObject> theObjects = this.getPlatformObjects();
		
		if (theObjects.size()>0)
			return theObjects.get(new Random().nextInt(theObjects.size()));
		else
			return null;
	}

	@Override
	public void deletePlatformObject(String gameID, String objectID, String objectType, String objectName) {
		try {
			switch (objectType) {
				case "POD":
					//client.pods().withName(objectName).delete();
					api.deleteNamespacedPod(objectName, namespace, null, null, null, null, null, null);
					break;
				case "SERVICE":
					//client.service().withName(objectName).delete();
					break;
				case "PVC":
					//client.().withName(objectName).delete();
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<GameObject> getPods() {
		List<GameObject> thePods = new ArrayList<>();
		try {
			V1PodList pods = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);
			for (V1Pod item : pods.getItems()) {
				thePods.add(new GameObject(item.getMetadata().getUid(), item.getMetadata().getName(), GameObject.TYPE.POD));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thePods;
	}


	private List<GameObject> getPVs() {
		List<GameObject> thePVs = new ArrayList<>();
		try {
			V1PersistentVolumeClaimList pvs = api.listNamespacedPersistentVolumeClaim(namespace, true, null,null,null,null,null
					,null,null,false);

			for (V1PersistentVolumeClaim item : pvs.getItems()) {
				thePVs.add(new GameObject(item.getMetadata().getUid(), item.getMetadata().getName(), GameObject.TYPE.PVC));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thePVs;
	}

	private List<GameObject> getServices() {
		List<GameObject> theServices = new ArrayList<>();
		try {
			V1ServiceList services = api.listNamespacedService(namespace, true, null, null, null, null, null, null, null, null);

			for (V1Service item : services.getItems()) {
				theServices.add(new GameObject(item.getMetadata().getUid(), item.getMetadata().getName(), GameObject.TYPE.SERVICE));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return theServices;
	}

}
