package com.example.kurlyflow.working.picking.request

data class MoveProductsToNewToteRequest(
    var batchId: Long,
    var oldToteId: String,
    var newToteId: String,
    var invoiceProductIds: ArrayList<String>
)
