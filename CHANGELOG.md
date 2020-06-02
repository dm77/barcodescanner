# Change Log

## [1.9.13] - February  16, 2019
* Update plugin, build tools and library versions
* Min SDK version is now 14
* Upload artifacts to jcenter/bintray instead of Maven central/Sonatype

## [1.9.8] - August 18, 2017
* One more attempt to fix Nexus 5x portrait scanning problems

## [1.9.7] - August 2, 2017
* Fix everything that was broken since 1.9.5 (Relevant issues: #336, #315, #339, #338)

## [1.9.6] - August 1, 2017
* Revert changes from 1.9.5 as it causes more harm than good (See #336)

## [1.9.5] - July 28, 2017
* Fix incorrectly rotated data on Nexus 5x devices. Thanks to @rramprasad for the pull request #315

## [1.9.4] - July 15, 2017
* Add ability to customize aspect ratio tolerance that is used in figuring out the optimal camera preview size. This is just a temporary fix for #249,#287,#293

## [1.9.3] - May 27, 2017
* Add ability to customize view finder with custom attributes. See #285 for more info. Thanks to @albinpoignot for the pull request

## [1.9.2] - May 13, 2017
* Add support for AZTEC codes in ZXing: #299, #288.

## [1.9.1] - April 8, 2017
* Add ability to scan inverted/negative codes with ZXing. Thanks to @manijshrestha for this pull request #265

## [1.9] - July 25, 2016
* Scale camera preview when the view size isn't full screen. Thanks to @xolan for this pull request PR #219
* Fix inverted camera in devices with differently oriented back and forward facing cameras. Thanks to @thadcodes for PR #191
* Add ability switch view finder view to square. Thanks to @squeeish for PR #163

## [1.8.4] - Dec 30, 2015
* Improve performance by opening camera and handling preview frames in a separate HandlerThread (#1, #99)
* Do not automatically stopCamera after a result is found #115
* Update samples to use Material Theme and make sure all samples use the FullScreen theme
* Update gradle wrapper to v2.10, gradle plugin to v1.5.0, buildToolsVersion to v23.0.2 and targetSdkVersion 23

## [1.8.3] - October 3, 2015
* Rebuild ZBar libraries with position independent code (#123,#119,#94).
