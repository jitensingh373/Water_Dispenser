package com.digitalsolution.waterdispenser.activity.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class UserConsumptionDetails {
    var DISPENSERNAME: String? = null
    var DISPENSETIME: String? = null
    var ID: String? = null
    var TIMESTAMP: Long? = null
    var USERNAME: String? = null
    var WATERQTY: String? = null
    var WATERTYPE: String? = null

    constructor(dispenserName: String?, dispenseTime: String?, uniqueKey: String?, timeStamp: Long?, userName: String?, waterQuantity: String?, waterType: String?) {
        this.DISPENSERNAME = dispenserName
        this.DISPENSETIME = dispenseTime
        this.ID = uniqueKey
        this.TIMESTAMP = timeStamp
        this.USERNAME = userName
        this.WATERQTY = waterQuantity
        this.WATERTYPE = waterType
    }

    constructor() {}

}