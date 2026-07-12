# qbit-standard-process-trace

## Knowledge base

A reviewed dossier for this repo lives in the second-brain vault:

- Hub: `$SECOND_BRAIN_VAULT/knowledge/qqq/qqq-hub.md` — map of the whole QQQ knowledge base
- Dossier: `$SECOND_BRAIN_VAULT/knowledge/qqq/repos/qbit-standard-process-trace.md`
  (purpose, API surface, data model, QBit contract usage, licensing state, v4.0 impact)

Reviewed at commit `0f2ec0339048` (branch `develop`, 2026-07-04).

Key facts worth knowing before editing:

- `develop` and `main` diverged in Sep 2025: `main` has Java 21 + Apache-2.0 LICENSE +
  qbit-build-parent 1.5.1 but is missing the backend-activity-stats feature; `develop`
  has the feature + tests but Java 17 parent 1.1.0, AGPL LICENSE, and a
  `qqq-bom-pom:0.40.0-SNAPSHOT` dependency. Reconcile before releasing.
- Tests use `QInstance.setAuthentication(...)`, which is removed in qqq 4.0
  (BREAK-04-11) — migrate to `registerAuthenticationProvider(AuthScope, ...)` when bumping qqq.
- CHANGELOG.md text is copy-pasted from a different QBit; README code examples have drift
  (nonexistent `producer.withTableMetaDataCustomizer`, table-name typos).
