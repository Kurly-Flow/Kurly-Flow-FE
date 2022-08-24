package com.example.kurlyflow.working.picking.model

data class PickingProductModel(
    var invoiceProductId: String,
    var name: String,
    var imageUrl: String,
    var quantity: Integer,
    var weight: Double,
    var region: String,
    var location: String
)
