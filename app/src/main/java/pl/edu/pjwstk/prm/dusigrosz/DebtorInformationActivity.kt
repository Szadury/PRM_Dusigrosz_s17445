package pl.edu.pjwstk.prm.dusigrosz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_debtor.*


class DebtorInformationActivity : AppCompatActivity() {
    private var isUpdate = false
    private var positionUpdate: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debtor)
        isUpdate = this.intent.extras!!.getBoolean("isUpdate")
        if (isUpdate) {
            positionUpdate = this.intent.extras!!.getInt("position")
            firstNameEdit.setText(this.intent.extras!!.getString("fName"))
            lastNameEdit.setText(this.intent.extras!!.getString("lName"))
            debtEdit.setText(this.intent.extras!!.getDouble("debt").toString())
        }
    }

    private fun isValid(firstName: String, lastName: String, debt: Double?): Boolean {
        return !firstName.isBlank() && !lastName.isBlank() && debt != null && debt >= 0
    }

    fun onAcceptClick(v: View) {
        val firstName = firstNameEdit.text.toString()
        val lastName = lastNameEdit.text.toString()
        val debt = debtEdit.text.toString().trim().toDoubleOrNull()
        if (isValid(firstName, lastName, debt)) {
            if (isUpdate) {
                val returnIntent = Intent()
                returnIntent.putExtra("result", "Updated")
                returnIntent.putExtra("position", positionUpdate)
                returnIntent.putExtra("fName", firstName)
                returnIntent.putExtra("lName", lastName)
                returnIntent.putExtra("debt", debt)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            } else {
                val returnIntent = Intent()
                returnIntent.putExtra("result", "Created")
                returnIntent.putExtra("fName", firstName)
                returnIntent.putExtra("lName", lastName)
                returnIntent.putExtra("debt", debt)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        } else {
            Toast.makeText(applicationContext, "Information are not valid", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun onCancelClick(v: View) {
        val returnIntent = Intent()
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Do you want to cancel?")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ ->
                setResult(Activity.RESULT_CANCELED, returnIntent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Cancelling debt creation/editing")
        alert.show()
    }

    fun onSimulationClick(v: View) {
        val firstName = firstNameEdit.text.toString()
        val lastName = lastNameEdit.text.toString()
        val debt = debtEdit.text.toString().trim().toDoubleOrNull()
        if (isValid(firstName, lastName, debt)) {
            val intent = Intent(this, DebtSimulationActivity::class.java)
            intent.putExtra("debtorInformation", "$firstName lastName debt: $debt")
            intent.putExtra("debt", debt)
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, "Invalid information", Toast.LENGTH_SHORT).show()
        }

    }

}