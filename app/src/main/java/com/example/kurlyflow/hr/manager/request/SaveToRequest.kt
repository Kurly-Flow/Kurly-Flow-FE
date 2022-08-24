package com.example.kurlyflow.hr.manager.request

import java.time.LocalDate

data class SaveToRequest(
    var workingDate: String,
    var workingTeam: String,
    var workingNumbers: Integer
)
