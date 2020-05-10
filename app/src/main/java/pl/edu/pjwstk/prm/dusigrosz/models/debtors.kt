package pl.edu.pjwstk.prm.dusigrosz.models

data class Debtor(var firstName: String, var lastName: String, var debt: Double) {
    override fun toString(): String {
        return "$firstName $lastName debt: $debt zł"
    }

}