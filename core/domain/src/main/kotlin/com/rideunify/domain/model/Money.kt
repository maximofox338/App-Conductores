package com.rideunify.domain.model

data class Money(
    val amount: Double,
    val currency: String = "ARS",
)
