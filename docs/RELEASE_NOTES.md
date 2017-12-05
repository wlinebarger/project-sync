# Release Notes

<!-- RELEASE NOTE FORMAT

1. Please use the following format for the release note subtitle
### {version} - {date}

2. link to commits of release.
3. link to docker image of release.
4. link to deployed worker.

5. Depending on the contents of the release use the subtitles below to 
  document the new changes in the release accordingly. Please always include
  a link to the releated issue number. 
   **New Features** (n)
   **Beta Features** (n)
   **Major Enhancements** (n)
   **Breaking Changes** (n)
   **Enhancements** (n)
   **Doc Fixes** (n)
   **Critical Bug Fixes** (n)
   **Bug Fixes** (n)
   **Hotfix** (n)
   - **Category Sync** - Sync now supports product variant images syncing. [#114](https://github.com/commercetools/commercetools-sync-java/issues/114)
   - **Build Tools** - Convinient handelling of env vars for integration tests.

6. Add Compatibility notes section, which specifies explicitly if there
are breaking changes. If there are, then a migration guide should be provided.

-->
### 1.1.0 - Nov 10, 2017
[Commits](https://github.com/commercetools/commercetools-sync-java/commits/1.0.0...1.1.0) |
[Docker Image](https://hub.docker.com/r/ctpcoeur/category-sync/) | 
[iron.io worker](https://hud-e.iron.io/worker/projects/57baae114efcd50007b84e66/codes/5a05814aa5d018000ae4e8e0)
 
**Enhancements** (1)
 - **Category Sync** - Use commercetools-sync-java v1.0.0-M6. 
 
**Bug Fixes** (1)
 - **Commons** - Fixed credential fetching through properties file. [#10](https://github.com/commercetools/project-coeur-sync/pull/10)

### 1.0.0 - Nov 10, 2017
[Commits](https://github.com/commercetools/commercetools-sync-java/commits/1.0.0) |
[Docker Image](https://hub.docker.com/r/ctpcoeur/category-sync/) | 
[iron.io worker](https://hud-e.iron.io/worker/projects/57baae114efcd50007b84e66/codes/5a05814aa5d018000ae4e8e0)
 
**New Features** (1)
 - **Category Sync** - Provide first version of the category sync from source to target CTP project. [#2](https://github.com/commercetools/project-coeur-sync/pull/2)