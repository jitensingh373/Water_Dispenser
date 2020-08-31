package com.digitalsolution.waterdispenser.activity.data

import com.google.firebase.database.Exclude

class UserConsumptionDetails {
    var dispenserName: String? = null
    var dispenseTime: String? = null
    @get:Exclude
    var uniqueKey: String? = null
    var timeStamp: Long? = null
    var userName: String? = null
    var waterQuantity: String? = null
    var waterType: String? = null

    constructor(dispenserName: String?, dispenseTime: String?, uniqueKey: String?, timeStamp: Long?, userName: String?, waterQuantity: String?, waterType: String?) {
        this.dispenserName = dispenserName
        this.dispenseTime = dispenseTime
        this.uniqueKey = uniqueKey
        this.timeStamp = timeStamp
        this.userName = userName
        this.waterQuantity = waterQuantity
        this.waterType = waterType
    }

    constructor() {}

}