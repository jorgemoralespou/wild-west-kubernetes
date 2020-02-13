package com.vmware.wildwest.models;

public class GameObject {

	private String id;
	private String name;
	private TYPE type;

	public enum TYPE {
		POD, SERVICE, PVC
	}
	
	public GameObject(String id, String name, TYPE type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}
	public GameObject(String id, String name, String type) {
		this.id = id;
		this.name = name;
		this.type = typeFromString(type);
	}

	private TYPE typeFromString(String type) {
		if (type.equalsIgnoreCase("pod")){
			return TYPE.POD;
		}else if (type.equalsIgnoreCase("service") || type.equalsIgnoreCase("svc")){
			return TYPE.SERVICE;
		}else if (type.equalsIgnoreCase("pvc")){
 			return TYPE.PVC;
		}else throw new RuntimeException(("Provided type as string is not valid: " + type));
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TYPE getType() {
		return type;
	}
	public void setType(TYPE type) {
		this.type = type;
	}
	public void setType(String type) {
		this.type = typeFromString(type);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GameObject that = (GameObject) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		return type == that.type;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}
