package nl.trifork.coins.coreapi

import java.math.BigDecimal

data class CoinDto(val currency: String, val price : BigDecimal)
data class QuoteRequestDto(val userId: String, val fromCurrency: String, val toCurrency: String, val amount : BigDecimal )
data class QuoteDto(val id : String, val fromCurrency: String, val toCurrency: String, val amount : BigDecimal,  val price : BigDecimal )
data class OrderRequestDto(val userId: String, val quoteId: String )
data class OrderDto(val id : String, val currency: String, val amount : BigDecimal,  val price : BigDecimal, val status: OrderStatus)
enum class OrderStatus { CREATED, PENDING, COMPLETED, FAILED }
data class UserRequestDto(val userId : String)
data class LedgerDto(val id: String, val assets : Map<String,BigDecimal>)