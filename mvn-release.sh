#!/bin/bash
mvn -DaltDeploymentRepository=snapshot-repo::default::file:releases clean deploy
