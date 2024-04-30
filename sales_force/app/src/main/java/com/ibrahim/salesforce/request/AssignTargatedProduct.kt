package com.app.salesforce.request

class AssignTargatedProduct{
    var assignProducts: List<RetailerProducts>?=null

    class RetailerProducts{
        var RetailerID:Int?=null
        var ProductList:List<Products>?=null
    }
    class Products{
        var ProductId:Int?=null
        var SeriesId:Int?=null
        var ClassId:Int?=null
    }



}

