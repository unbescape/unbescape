# Releasing Unbescape

## 1. Configuration

Configure access to the Central Portal in `settings.xml`: `central` is used for releases
and `central-portal-snapshots` for snapshots.

```xml
  <servers>
    <server>
      <id>central</id>
      <username>[CENTRAL PORTAL TOKEN USERNAME]</username>
      <password>[CENTRAL PORTAL TOKEN PASSWORD]</password>
    </server>
    <server>
      <id>central-portal-snapshots</id>
      <username>[CENTRAL PORTAL TOKEN USERNAME]</username>
      <password>[CENTRAL PORTAL TOKEN PASSWORD]</password>
      <repositoryOrigins>
        <repositoryOrigin>https://central.sonatype.com</repositoryOrigin>
      </repositoryOrigins>
    </server>
  </servers>
```

Without `repositoryOrigins`, Maven 3.10+ warns about the `central-portal-snapshots` server
and sends its credentials to any repository declared with that id. `central` needs no
`repositoryOrigins`, because Maven binds it to `https://repo.maven.apache.org` automatically.

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
mvn -P releases clean compile deploy
```

## 5. Create tag and set to new development version

```shell
mvn scm:tag -Dtag=unbescape-X.Y.Z
mvn versions:set -DprocessAllModules=true -DnewVersion=X.Y.[Z+1]-SNAPSHOT
mvn versions:commit
git add .
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



