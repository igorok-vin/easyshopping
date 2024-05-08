# jQuery Image Resolution Checker

> Simple jQuery plugin to check Image Resolution upon input-file selection

This plugin will show an error if the selected image in an HTML Input is not in the intended resolutions.

## Installation

Include the plugin file after initializing jQuery:

```
<script src="jquery.js"></script> 
<script src="jquery.checkImageSize.min.js"></script>
```

## Basic Usage

Apply to all file-input :
```
$("input[type=file]").checkImageSize();
```

Apply to specific file-input with class **check-image-size** :

```
$("input[type=file].check-image-size").checkImageSize();
```

### File-Input Configurations

Simple File-Input with default options :
```
<input type="file" class="check-image-size" >
```

File-Input with custom options :
```
<input type="file" class="check-image-size" data-min-width="800" data-min-height="600" data-max-width="8000" data-max-height="6000" >
```

## Options

```
$("input[type=file]").checkImageSize({
    minWidth: 800,		// Numeric; Pixel value
    minHeight: 600,		// Numeric; Pixel value
    maxWidth: 8000,		// Numeric; Pixel value
    maxHeight: 6000,		// Numeric; Pixel value
    showError: true,		// Boolean; Whether to show error messages
    ignoreError: false		// Boolean; Whether to ignore error and let the image pass through
});
```

----

Made with Love by **Jose Philip Raja** &mdash; Founder & Creative Director of **CreaveLabs IT Solutions LLP**
http://josephilipraja.com, https://creavelabs.com
