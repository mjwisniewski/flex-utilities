/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


var request = require('request');
var fs = require('fs');
var events = require('events');
var prompt = require('prompt');
var unzip = require('unzip');

var constants = require('../dependencies/Constants');

var AdobeAIR = module.exports = Object.create(events.EventEmitter.prototype);

//Adobe AIR
var AdobeAIRURL = 'http://airdownload.adobe.com/air/win/download/19.0/';
var fileNameAdobeAIR = 'AdobeAIRSDK.zip';
var adobeAirPromptText = "\
Apache Flex SDK uses the Adobe AIR SDK to build Adobe AIR applications.\n\
The Adobe AIR SDK is subject to and governed by the\n\
Adobe AIR SDK License Agreement specified here:\n\
http://www.adobe.com/products/air/sdk-eula.html.\n\
    This license is not compatible with the Apache v2 license.\n\
Do you want to download and install the Adobe AIR SDK? (y/n)";

function promptForAdobeAIR()
{
    var schema = {
        properties: {
            accept: {
                description: adobeAirPromptText.magenta,
                pattern: /^[YNyn\s]{1}$/,
                message: 'Please respond with either y or n'.red,
                required: true
            }
        }
    };
    prompt.start();
    prompt.get(schema, function (err, result) {
        if(result.accept.toLowerCase() == 'y')
        {
            downloadAdobeAIR();
        }
        else
        {
            console.log('Aborting installation');
        }
    });
}

function downloadAdobeAIR()
{
    /*var downloadDetails = {
        url:AdobeAIRURL,
        remoteFileName:fileNameAdobeAIR,
        destinationPath:constants.DOWNLOADS_FOLDER,
        destinationFileName:'adobeair',
        unzip:true
    };
    duc.on('installComplete', handleInstallComplete);
    duc.install(downloadDetails);*/

    console.log('Downloading Adobe AIR from ' + AdobeAIRURL + fileNameAdobeAIR);
    request
        .get(AdobeAIRURL + fileNameAdobeAIR)
        .pipe(fs.createWriteStream(constants.DOWNLOADS_FOLDER + fileNameAdobeAIR)
            .on('finish', function(){
                console.log('Adobe AIR download complete');
                extract();
            })
    );
}

function extract()
{
    console.log('Extracting Adobe AIR');
    fs.createReadStream(constants.DOWNLOADS_FOLDER + fileNameAdobeAIR)
        .pipe(unzip.Extract({ path: constants.FLEXJS_FOLDER })
            .on('finish', function(){
                console.log('Adobe AIR extraction complete');
                AdobeAIR.emit('complete');
            })
    );
}

AdobeAIR.install = function()
{
    promptForAdobeAIR();
};