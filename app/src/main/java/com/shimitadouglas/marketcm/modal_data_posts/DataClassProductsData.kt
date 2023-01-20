package com.shimitadouglas.marketcm.modal_data_posts

data class DataClassProductsData(
    var keyImageProduct: Int,
    var keyCircleImageOwner: Int,
    var keyTitleProduct: String,
    var keyProductOwner: String,
    var keyDescription: String,
    var keyCategory: String,
    var keyProductID: String,
    var keyVicinity: String,
    var keyDate:String
) {

    val imageProduct = keyImageProduct
    val imageOwner = keyCircleImageOwner
    val titleProduct = keyTitleProduct
    val productOwner = keyProductOwner
    val productDescription = keyDescription
    val categoryProduct = keyCategory
    val productID = keyProductID
    val vicinityProduct = keyVicinity
    val date=keyDate

}

//todo:when really fetching the data from fStore ensure no passing the keys via constructor but inside the class with the
//todo:keys that matches data @fStore
