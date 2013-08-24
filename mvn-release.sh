#!/bin/bash
mvn versions:set
mvn -DaltDeploymentRepository=snapshot-repo::default::file:releases clean deploy
rm pom.xml.versionsBackup