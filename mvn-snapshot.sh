#!/bin/bash
mvn -DaltDeploymentRepository=snapshot-repo::default::file:snapshots clean deploy
