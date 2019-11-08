cordova.define("com.speedata.cordova.plugin.UHF.UHF", function(require, exports, module) {
var uhf = {
    openDev: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'openDev', []);
    }
    ,
    closeDev: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'closeDev', []);
    }
    ,
    readArea: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'readArea', tags);
    }
    ,
    getReadAreaResult: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'getReadAreaResult', []);
    }
    ,
    inventoryStart: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'inventoryStart', []);
    }
    ,
    inventoryStop: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'inventoryStop', []);
    }
    ,
    getInventoryResult: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'getInventoryResult', []);
    }
    ,
    writeArea: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'writeArea', tags);
    }
    ,
    getWriteAreaResult: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'getWriteAreaResult', []);
    }
    ,
    setAntennaPower: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'setAntennaPower', tags);
    }
    ,
    getAntennaPower: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'getAntennaPower', []);
    }
    ,
    setFreqRegion: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'setFreqRegion', tags);
    }
    ,
    getFreqRegion: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'getFreqRegion', []);
    }
    ,
    selectCard: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'UHF', 'selectCard', tags);
    }
};
module.exports = uhf;
});
