package com.digitalsolution.waterdispenser.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class LoginCredential {
    var DEVICE_TOKEN: String? = null
    var ID: String? = null
    var PASSCODE: String? = null
    var ROLE: String? = null
    var TIMESTAMP: String? = null
    var USERNAME: String? = null
    constructor() {}
}