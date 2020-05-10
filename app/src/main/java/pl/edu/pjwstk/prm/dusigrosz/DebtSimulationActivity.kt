package pl.edu.pjwstk.prm.dusigrosz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_simulation.*
import java.util.concurrent.atomic.AtomicBoolean


class DebtSimulationActivity : AppCompatActivity() {
    private val isRunning = AtomicBoolean()
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            val debtLeft = bundle.getString("debtLeft")
            if (0.0.toString() == debtLeft) {
                interestLabel.visibility = View.VISIBLE
                debtInterestValue.visibility = View.VISIBLE
            }
            debtAmountLeft.text = debtLeft.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulation)
        val debtTextView = findViewById<TextView>(R.id.debt_info)
        debtTextView.text = intent.extras!!.getString("debtorInformation")
        debtAmountLeft.text = intent.extras!!.getDouble("debt", 0.0).toString()
    }

    private fun isValid(debt: Double?, plnPerSec: Double?, commisionPercent: Int?): Boolean {
        return (debt != null && debt > 0 && plnPerSec != null && plnPerSec > 0 && commisionPercent != null && commisionPercent > 0)
    }

    fun onSimulateStart(v: View) {
        val debt = debtAmountLeft.text.toString().toDouble()
        val comissionPercent = commision.text.toString().toInt()
        val payPerSecond = plnPerSecText.text.toString().toDoubleOrNull()
        if (isValid(debt, payPerSecond, comissionPercent)) {
            if (!isRunning.get()) {
                startSimulation(debt, payPerSecond, comissionPercent)
            }
        } else {
            Toast.makeText(applicationContext, "Data provided is invalid", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun startSimulation(debt: Double?, payPerSecond: Double?, commisionPercent: Int) {
        val commisionInDecimal: Double = 1 + commisionPercent / 100.0
        val th = Thread(Runnable {
            var debtLeft = debt
            while (debtLeft != 0.0 && isRunning.get()) {
                if (debtLeft?.minus(payPerSecond!!)!! > 0) {
                    debtLeft = (debtLeft.minus(payPerSecond!!)).times(commisionInDecimal)
                    val msg = handler.obtainMessage()
                    val bundle = Bundle()
                    println(debtLeft)
                    bundle.putString("debtLeft", debtLeft.toString())
                    msg.data = bundle
                    handler.sendMessage(msg)
                } else {
                    debtLeft = 0.0
                    val debtAfterSecond = 0.0
                    val msg = handler.obtainMessage()
                    val bundle = Bundle()
                    println(debtAfterSecond)
                    bundle.putString("debtLeft", debtAfterSecond.toString())
                    msg.data = bundle
                    handler.sendMessage(msg)
                }
                Thread.sleep(1000)
            }
            isRunning.set(false)
        })
        th.start()
    }

}