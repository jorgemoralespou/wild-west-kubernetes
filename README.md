# Wild West Kubernetes

![](https://github.com/gshipley/wild-west-kubernetes/raw/master/src/main/resources/static/assets/sceenshot.png)

Wild West Kubernetes is a sample application written in Spring Boot and the Phaser game engine to make killing pods fun.  In order to play the game, you need to have a kubernetes cluster running and issue the following command:

```
kubectl apply -f ./deploy/kubernetes/k8s.yaml
```

This will create a namespace called *wildwest* and deploy the gshipley/wildwest:latest docker image with five replicas.  This will also create a service and apply the correct RBAC view role to pull information from the kubernetes API.  It should be noted that the default deploy/kubernetes/k8s.yaml file creates a service using a NodePort. This should work in most minikube environments but you should change it if you are running on a cluster with a LoadBalancer.  For an example of an ingress, check out the deploy/kubernetes/ingress.yaml file.

By default, the game doesn't actually destroy the pods when you shoot them.  If you want to enable destructive mode, issue the following command:

```
kubectl apply -f ./deploy/kubernetes/destructive.yaml
```

Once you have the game deployed, you will need to expose the service so that you can access the web application.  If you are using minikube, you could use port-fowarding:

```
kubectl port-forward -n wildwest svc/wildwest 8080:8080
```

Happy Pod hunting.

## k14s - Kubernetes tools

### Use kapp for deploying your app
We find [kapp](https://get-kapp.io/) to be a better tool than kubectl for deployment of your application in Kubernetes. If you have *kapp* installed, you can easily try this application:

```bash
kapp deploy -a wild-west -f ./deploy/kubernetes/k8s.yaml -y
```

### Use ytt for deploying your app
[ytt](https://get-ytt.io/) is a fantastic templating engine for Kubernetes that gives us a lot of flexibility. If you have *ytt* installed, you can easily customize your deployment:

```bash
ytt -f deploy/k14s | kapp deploy -y --diff-changes -a wild-west -f-
```

#### Customize any value
If you want to customize any of the possible values you can, either provide a values.yml file or provide specific values via command line:

```bash
ytt -f deploy/k14s --data-value-yaml namespace.name=k8s-wildwest | kapp deploy -y --diff-changes -a wild-west -f-
```

### Use kbld to build your container image
If you're in the building process, and you want to use the image you're building in the the deployment, [kbld](https://get-kbld.io/) is your tool. If you have *kbld* installed, you can use it very easily:

```bash
ytt -f deploy/k14s --data-value-yaml namespace.name=k8s-wildwest  --data-value-yaml image.build=true | kbld -f - | kapp deploy -y --diff-changes -a wild-west -f-
```

If you want to build your container by compiling locally your java application (faster), use:

```bash
mvn package
ytt -f deploy/k14s --data-value-yaml namespace.name=k8s-wildwest  --data-value-yaml image.build=true --data-value-yaml dockerfile=docker/Dockerfile.innerloop | kbld -f - | kapp deploy -y --diff-changes -a wild-west -f-
```

If you don't have maven locally, or you want to build your application archive (.jar file) it in a container, use:
```bash
ytt -f deploy/k14s --data-value-yaml namespace.name=k8s-wildwest  --data-value-yaml image.build=true --data-value-yaml dockerfile=docker/Dockerfile.outerloop | kbld -f - | kapp deploy -y --diff-changes -a wild-west -f-
```

### Delete your application
If you want to delete your application, it can be done in an easy command, anywhere:

```bash
kapp delete -a wild-west
```

## Build your container
There's 3 ways to build your container images:

* Build your application locally and then create the container image with a Docker multi-stage build. This will mostly be used for local development as it benefits from maven cache
* Build your application and container image with a Docker multi-stage build. This will be slower, but it doesn't depend on having maven installed.
* Build your application and image with a Buildpack

### Option 1: Inner loop build

```
mvn package
docker build -t "k8s/wildwest:innerloop" -f docker/Dockerfile.innerloop .
docker tag k8s/wildwest:innerloop k8s/wildwest:latest
```

### Option 2: Outer loop build

```
docker build -t "k8s/wildwest:outerloop" -f docker/Dockerfile.outerloop .
docker tag k8s/wildwest:outerloop k8s/wildwest:latest
```


### Option 3: Inner/Outer loop build

```
mvn clean package spring-boot:build-image
docker tag docker.io/library/wildwest:1.0 k8s/wildwest:latest
```



## Install and use tekton pipelines

### Install tekton
Tekton will install all of it's namespaced components into the tekton-pipelines namespace, the rest will be installed clusterwide.

```bash
kubectl apply -f https://storage.googleapis.com/tekton-releases/pipeline/latest/release.yaml
kubectl apply -f https://github.com/tektoncd/dashboard/releases/download/v0.4.1/dashboard_latest_release.yaml
kubectl apply -f -<<EOF
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: tekton-dashboard
  namespace: tekton-pipelines
spec:
  rules:
  - host: dashboard.tekton.test
    http:
      paths:
      - backend:
          serviceName: tekton-dashboard
          servicePort: 9097
EOF
kubectl apply --filename https://storage.googleapis.com/tekton-releases/triggers/latest/release.yaml
```

There's a [catalog of Tekton tasks](https://github.com/tektoncd/catalog) with very useful and reusable tasks.