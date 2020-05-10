package pl.edu.pjwstk.prm.dusigrosz

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import pl.edu.pjwstk.prm.dusigrosz.models.Debtor
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureDebtorsList()
        countDebtSum()
    }

    private fun configureDebtorsList() {
        val debtorList = ArrayList<Debtor>()
        val debtorsListView = findViewById<ListView>(R.id.debtorsListView)
        debtorsListView.setOnItemLongClickListener { _, _, position, _ ->
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Do you want to delete this debt?")
                .setCancelable(true)
                .setPositiveButton("Delete") { _, _ ->
                    deleteDebtor(position)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.show()
            true
        }
        debtorsListView.setOnItemClickListener { _, _, position, _ ->
            Log.v("Clicked position", "pos: $position")
            val intent = Intent(this, DebtorInformationActivity::class.java)
            intent.putExtra("isUpdate", true)
            intent.putExtra("position", position)
            val debtor = getDebtorByPosition(position)
            intent.putExtra("fName", debtor?.firstName)
            intent.putExtra("lName", debtor?.lastName)
            intent.putExtra("debt", debtor?.debt)
            startActivityForResult(intent, 200)
        }
        val debtorsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, debtorList)
        debtorsListView.adapter = debtorsAdapter
    }

    private fun countDebtSum() {
        var debtSum = 0.0
        val debtors = getDebtorsList()
        for (debtor in debtors) {
            debtSum += debtor.debt
        }
        val res: Resources = resources
        findViewById<TextView>(R.id.totalDebtLabel).text =
            res.getString(
                R.string.debtSumCounter,
                BigDecimal(debtSum).setScale(2, RoundingMode.HALF_EVEN)
            )
    }

    fun onAddDebtorClick(v: View) {
        val intent = Intent(this, DebtorInformationActivity::class.java)
        intent.putExtra("isUpdate", false)
        startActivityForResult(intent, 200)
    }

    private fun getDebtorsList(): MutableList<Debtor> {
        val list = ArrayList<Debtor>()
        val adapter = findViewById<ListView>(R.id.debtorsListView).adapter
        for (i in 0 until adapter.count) {
            list.add((adapter.getItem(i) as Debtor))
        }
        return list
    }

    private fun getDebtorByPosition(position: Int): Debtor? {
        val debtors = getDebtorsList()
        return if (debtors.size > position && position > -1) {
            debtors[position]
        } else {
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val result = data.getStringExtra("result")
                val firstName = data.getStringExtra("fName")
                val lastName = data.getStringExtra("lName")
                val debt = data.getDoubleExtra("debt", 0.0)
                if (result == "Updated") {
                    val position = data.getIntExtra("position", 0)
                    editDebtor(Debtor(firstName, lastName, debt), position)
                } else if (result == "Created") {
                    addDebtor(Debtor(firstName, lastName, debt))
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Creating/Editing was failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(applicationContext, "Creating/Editing was cancelled", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun addDebtor(debtor: Debtor) {
        val debtors = getDebtorsList()
        debtors.add(debtor)
        val debtorsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, debtors)
        val debtorsListView = findViewById<ListView>(R.id.debtorsListView)
        debtorsListView.adapter = debtorsAdapter
        countDebtSum()
    }

    private fun editDebtor(debtor: Debtor?, position: Int?) {
        val debtors = getDebtorsList()
        if (debtors.size > position!! && position > -1) {
            if (debtor != null) {
                debtors[position] = debtor
            }
        } else {
            Toast.makeText(applicationContext, "Editing failed", Toast.LENGTH_SHORT).show()
        }
        val debtorsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, debtors)
        val debtorsListView = findViewById<ListView>(R.id.debtorsListView)
        debtorsListView.adapter = debtorsAdapter
        countDebtSum()
    }

    private fun deleteDebtor(position: Int) {
        val debtors = getDebtorsList()
        if (debtors.size > position && position > -1) {
            debtors.removeAt(position)
        } else {
            Toast.makeText(applicationContext, "Deleting failed", Toast.LENGTH_SHORT).show()
        }
        val debtorsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, debtors)
        val debtorsListView = findViewById<ListView>(R.id.debtorsListView)
        debtorsListView.adapter = debtorsAdapter
        countDebtSum()

    }
}
