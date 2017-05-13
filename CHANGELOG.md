# Change Log

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
