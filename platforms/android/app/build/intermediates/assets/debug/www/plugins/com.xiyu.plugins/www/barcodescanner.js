cordova.define("xiyu-plugin-bscan", function(require, exports, module) {
var exec = require('cordova/exec');

module.exports = {



    /**
     * Encoding constants.
     *
     * @type Object
     */
  EncodeType: {
        TEXT_TYPE: "TEXT_TYPE",
        EMAIL_TYPE: "EMAIL_TYPE",
        PHONE_TYPE: "PHONE_TYPE",
        SMS_TYPE: "SMS_TYPE"
        //  CONTACT_TYPE: "CONTACT_TYPE",  // TODO:  not implemented, requires passing a Bundle class from Javascript to Java
        //  LOCATION_TYPE: "LOCATION_TYPE" // TODO:  not implemented, requires passing a Bundle class from Javascript to Java
    },

    /**
     * Barcode format constants, defined in ZXing library.
     *
     * @type Object
     */
    format: {
        "all_1D": 61918,
        "aztec": 1,
        "codabar": 2,
        "code_128": 16,
        "code_39": 4,
        "code_93": 8,
        "data_MATRIX": 32,
        "ean_13": 128,
        "ean_8": 64,
        "itf": 256,
        "maxicode": 512,
        "msi": 131072,
        "pdf_417": 1024,
        "plessey": 262144,
        "qr_CODE": 2048,
        "rss_14": 4096,
        "rss_EXPANDED": 8192,
        "upc_A": 16384,
        "upc_E": 32768,
        "upc_EAN_EXTENSION": 65536
    },


/**
 * Read code from scanner.
 *
 * @param {Function} successCallback This function will recieve a result object: {
 *        text : '12345-mock',    // The code that was scanned.
 *        format : 'FORMAT_NAME', // Code format.
 *        cancelled : true/false, // Was canceled.
 *    }
 * @param {Function} errorCallback
 */
    Scan: function (successCallback, errorCallback, config) {

        if(config instanceof Array) {
            // do nothing
        } else {
            if(typeof(config) === 'object') {
                config = [ config ];
            } else {
                config = [];
            }
        }

        if (errorCallback == null) {
            errorCallback = function () {
            };
        }

        if (typeof errorCallback != "function") {
            console.log("BarcodeScanner.scan failure: failure parameter not a function");
            return;
        }

        if (typeof successCallback != "function") {
            console.log("BarcodeScanner.scan failure: success callback parameter must be a function");
            return;
        }

        exec(successCallback, errorCallback, 'BarcodeScanner', 'scan', config);
    },

//-------------------------------------------------------------------
    Encode : function (type, data, successCallback, errorCallback, options) {
        if (errorCallback == null) {
          errorCallback = function () {
          };
        }

        if (typeof errorCallback != "function") {
          console.log("BarcodeScanner.encode failure: failure parameter not a function");
          return;
        }

        if (typeof successCallback != "function") {
          console.log("BarcodeScanner.encode failure: success callback parameter must be a function");
          return;
        }

        exec(successCallback, errorCallback, 'BarcodeScanner', 'encode', [
          {"type": type, "data": data, "options": options}
        ]);
    }



};
});