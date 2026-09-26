# qbit-standard-process-trace

## Knowledge base

A reviewed dossier for this repo lives in the second-brain vault:

- Hub: `$SECOND_BRAIN_VAULT/knowledge/qqq/qqq-hub.md` — map of the whole QQQ knowledge base
- Dossier: `$SECOND_BRAIN_VAULT/knowledge/qqq/repos/qbit-standard-process-trace.md`
  (purpose, API surface, data model, QBit contract usage, licensing state, v4.0 impact)

Reviewed at commit `0f2ec0339048` (branch `develop`, 2026-07-04).

Key facts worth knowing before editing:

- `main` was merged back into `develop` for QRun-IO/qqq#766, so `develop` has main's
  Java 21 + Apache-2.0 LICENSE/NOTICE and the backend-activity-stats feature. The pom
  `<licenses>` and source headers still say AGPL; aligning them is a separate owner decision.
- The qqq version comes only from `qbit-build-parent` 2.0.0 (qqq 4.0.0); do not re-add a
  `qqq-bom-pom` import. `mvn -B verify -Pqqq-snapshot` checks against qqq `4.1.0-SNAPSHOT`
  (override with `-Dqqq.snapshot.version=...`).
- CHANGELOG.md text is copy-pasted from a different QBit; README code examples have drift
  (nonexistent `producer.withTableMetaDataCustomizer`, table-name typos).
