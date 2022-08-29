package com.example.kurlyflow.working.end.model

data class EndInvoiceModel(
    var invoiceId: String,
    var ordererName: String,
    var ordererAddress: String,
    var products : ArrayList<EndProductModel>
)
