package com.example.kurlyflow.working.picking.model

data class PickingModel(
    var recommendToteCount: Integer,
    var batchId: Long,
    var invoiceProductResponses: ArrayList<PickingProductModel>
)
