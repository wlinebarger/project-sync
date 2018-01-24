# project-coeur-sync

## TL;DR;

```bash
$ helm install .
```

## Introduction

This chart creates a coeur-sync deployment on a [Kubernetes](http://kubernetes.io) 
cluster using the [Helm](https://helm.sh) package manager.

## Prerequisites

- Kubernetes 1.8+ with Beta APIs enabled

## Installing the Chart

To install the chart with the release name `my-release`:

```bash
$ helm install --name my-release .
```

The command deploys coeur-sync on the Kubernetes cluster using the default configuration. The [configuration](#configuration) section lists the parameters that can be configured during installation.

## Uninstalling the Chart

To uninstall/delete the `my-release` deployment:

```bash
$ helm delete --purge my-release
```
The command removes all the Kubernetes components associated with the chart and deletes the release.

## Configuration

The following table lists the configurable parameters of the coeur-sync chart and their default values.

Parameter | Description | Default
--- | --- | ---
`restartPolicy` | container restart policy | `Never`
`image.repository` | container image repository | `eu.gcr.io/professionalserviceslabs/product-publish-state-import:latest`
`image.tag` | container image tag | `latest`
`image.pullPolicy` | container image pull policy | `IfNotPresent`
`resources` | resource requests & limits | requests: `cpu: 50m, memory: 256Mi`, limits: `cpu: 150m, memory: 512Mi}`
`ctpProjectName` | CTP project name. | `carharrt-demo-83`
`ctpClientId` | CTP  API client id | `dummyClientId`
`ctpClientId` | CTP API client secret. | `dummyClientSecret`

Specify each parameter using the `--set key=value[,key=value]` argument to `helm install`. For example,

```bash
$ helm install --name my-release \
    --set key_1=value_1,key_2=value_2 \
    .
```

Alternatively, a YAML file that specifies the values for the parameters can be provided while installing the chart. For example,

```bash
# example for staging
$ helm install --name my-release -f values.yaml .
```

> **Tip**: You can use the default [values.yaml](values.yaml)
