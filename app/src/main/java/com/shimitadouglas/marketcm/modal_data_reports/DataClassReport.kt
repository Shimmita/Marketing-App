package com.shimitadouglas.marketcm.modal_data_reports

data class DataClassReport(
    var ProductClaimID: String? = null,
    var claimDate: String? = null,
    var productCode: String? = null,
    var productControlUID: String? = null,
    var productMessage: String? = null,
    var productSuspectID: String? = null,
    var productVictimID: String? = null
)
