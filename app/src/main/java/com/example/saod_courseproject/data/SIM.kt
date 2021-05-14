package data

data class SIM (
        val simNumber: String,
        var tariff: String,
        val yearOfIssue: Int,
        var isUse: Boolean = false
)