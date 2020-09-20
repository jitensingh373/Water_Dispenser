package com.digitalsolution.waterdispenser.activity.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class UserConsumptionDetails {
    var DISPENSERNAME: String? = null
    var DISPENSETIME: String? = null
    var ID: String? = null
    var TIMESTAMP: String? = null
    var USERNAME: String? = null
    var WATERQTY: String? = null
    var WATERTYPE: String? = null
    var PLATFORMTYPE: String? = null
    constructor() {}

}