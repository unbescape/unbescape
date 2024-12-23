# Releasing Unbescape

## 1. Configuration

Configure access to the snapshots and staging servers in `settings.xml`:

```xml
  <servers>
    <server>
      <id>sonatype-nexus-snapshots</id>
      <username>[USER IN SONATYPE NEXUS]</username>
      <password>[ENCODED PASSWORD IN SONATYPE NEXUS]</password>
    </server>
    <server>
      <id>sonatype-nexus-staging</id>
      <username>[USER IN SONATYPE NEXUS]</username>
      <password>[ENCODED PASSWORD IN SONATYPE NEXUS]</password>
    </server>
  </servers>
```

## 2. Check dependency snapshots

If this is an GA or RC version of Unbescape being published, check that no
dependencies are in SNAPSHOT version.

If any SNAPSHOTs exist, set to a fixed version and commit.

## 3. Set the new version

```shell
mvn versions:set -DprocessAllModules=true -DnewVersion=X.Y.Z
mvn versions:commit
git add .
git commit -m "Prepare release unbescape-X.Y.Z"
git push
```

## 4. Create and deploy the release into the staging repositories

```shell
mvn clean compile deploy
```

## 5. Create tag and set to new development version

```shell
mvn scm:tag -Dtag=unbescape-X.Y.Z
mvn versions:set -DprocessAllModules=true -DnewVersion=X.Y.[Z+1]-SNAPSHOT
mvn versions:commit
git add .;
git commit -m "Prepare for next development iteration"
git push
```

## 6. Create tag and set to new development version

If any SNAPSHOT dependencies had to have their versions fixed before building, go
back to SNAPSHOT versions wherever needed and commit.

## 7. Manage staging repository in Central

Follow instructions at https://central.sonatype.org/publish/publish-guide/

## 8. Releasing distribution artifacts

Once jar artifacts have been published in Maven Central, the .zip distribution release
generated in dist/target should be uploaded to the GitHub repository as a new release
along with a gpg signature (.asc) generated with:

```shell
gpg -ab --default-key key_ID_for_releases@unbescape.org unbescape-X.Y.Z-dist.zip
```



