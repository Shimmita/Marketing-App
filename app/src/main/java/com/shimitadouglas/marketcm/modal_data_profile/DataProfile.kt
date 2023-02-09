package com.shimitadouglas.marketcm.modal_data_profile

import com.shimitadouglas.marketcm.mains.Registration

data class DataProfile(
    var Email: String? = null,
    var ImagePath: String? = null,
    var PhoneNumber: String? = null,
    var University:String?=null,
    var LastName:String?=null,
    var FirstName:String?=null,
    var Password:String?=null,
    var registrationDate: String?=null,
    var canPost:String?=null
)
