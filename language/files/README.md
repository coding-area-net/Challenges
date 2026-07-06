# Legacy Language Files (Backwards Compatibility)

⚠️ **DO NOT DELETE OR MOVE THIS FOLDER** ⚠️

## Purpose
This folder is strictly maintained for backwards compatibility with older versions of the **Challenges** plugin.

In versions **prior to v2.4**, the plugin did not bundle localization and translation files directly within the build. Instead, it pulled them dynamically at runtime from this GitHub repository, targeting this specific path (`/language/files/`).

## Usage & Maintenance
* **Who uses this?** Legacy installations running Challenges v2.3.x and below.
* **Format:** The JSON files in this folder use the legacy formatting required by older versions.
* **Updates:** Unless a critical bug fix is required for legacy users, these files should remain untouched. Modern translation assets should be managed in the updated translations directory.

---
*For modern versions (v2.4+), please refer to `/locales` directory.*
