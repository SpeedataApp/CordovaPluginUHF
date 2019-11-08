cordova.define('cordova/plugin_list', function(require, exports, module) {
  module.exports = [
    {
      "id": "com.speedata.cordova.plugin.UHF.UHF",
      "file": "plugins/com.speedata.cordova.plugin.UHF/www/uhf.js",
      "pluginId": "com.speedata.cordova.plugin.UHF",
      "clobbers": [
        "window.uhf"
      ]
    }
  ];
  module.exports.metadata = {
    "cordova-plugin-whitelist": "1.3.4",
    "com.speedata.cordova.plugin.UHF": "1.0.1"
  };
});