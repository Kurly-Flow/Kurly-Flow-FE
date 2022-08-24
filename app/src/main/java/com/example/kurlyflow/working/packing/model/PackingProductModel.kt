package com.example.kurlyflow.working.packing.model

data class PackingProductModel(
    var invoiceProductId: Long,
    var name: String,
    var quantity: Int,
    var packaging: String
)
