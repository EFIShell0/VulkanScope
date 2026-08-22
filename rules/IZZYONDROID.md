# IzzyOnDroid release policy audit

Checked against the current IzzyOnDroid documentation on 2026-08-22.

Authoritative policy sources:

- https://izzyondroid.org/about/security/ApkScans/
- https://izzyondroid.org/docs/general/Fastlane/
- https://izzyondroid.org/docs/general/YamlMetadata/

## Self-updater requirement

IzzyOnDroid tolerates REQUEST_INSTALL_PACKAGES for a non-package-manager only when the self-updater is clearly opt-in, disabled by default, and the user is told where APKs come from and that external APK updates bypass IzzyOnDroid screening.

This release therefore keeps direct GitHub updates disabled by default for every installation. Android installer identity is not used to infer the repository because an F-Droid-compatible client can install packages from multiple repositories and installer identity does not reliably identify IzzyOnDroid as the source repository.

Enabling direct updates is an explicit Settings action followed by a confirmation naming the official GitHub Releases source and explaining the IzzyOnDroid screening/verification bypass.

## Fastlane

The source release contains the required en-US short and full descriptions, title, icon and versionCode-matched changelog. Screenshots are included where existing project screenshots are available; they are recommended rather than mandatory.


## First-install direct-update notice (0.34.4)

A genuine first installation with direct updates disabled shows a one-time seven-second non-modal banner using the same visual language as update-status notifications. It only explains that direct GitHub updates are disabled by default and can be enabled optionally in Settings. The separate GitHub-source and IzzyOnDroid-bypass confirmation remains required before opt-in.
