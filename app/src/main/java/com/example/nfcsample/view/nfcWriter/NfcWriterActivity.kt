package com.example.nfcsample.view.nfcWriter

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcsample.databinding.ActivityNfcWriterBinding

class NfcWriterActivity : AppCompatActivity() {

    lateinit var binding: ActivityNfcWriterBinding
    val viewModel: NfcWriterViewModel by viewModels()
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNfcWriterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        checkNfcSupport()

    }

    override fun onResume() {
        super.onResume()
        enableForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent)
    }

    private fun checkNfcSupport() {
        if (nfcAdapter == null) {
            // Device doesn't support NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_SHORT).show()
            return
        }

        nfcAdapter?.let {
            if (it.isEnabled.not()) {
                // NFC is disabled
                Toast.makeText(this, "Please enable NFC in your device settings.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableForegroundDispatch() {
        val intent = Intent(this, javaClass).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val techList = arrayOf(arrayOf(NfcA::class.java.name)) // Filter for Mifare Classic
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, techList)
    }

    private fun disableForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
            checkMifareClassicTag(tag)
        }
    }

    private fun checkMifareClassicTag(tag: Tag) {
//        if (tag.techList.contains(NfcA::class.java)) {
        if (tag.techList.contains("android.nfc.tech.NfcA")) {
            // Mifare Classic tag detected
            Toast.makeText(this, "Mifare Classic tag detected!", Toast.LENGTH_SHORT).show()
            // You can't directly write text using NDEF to Mifare Classic.
            // Further processing (like reading specific sectors) might require lower-level Mifare Classic communication libraries.
        } else {
            Toast.makeText(this, "Tag is not a Mifare Classic tag.", Toast.LENGTH_SHORT).show()
        }
    }
}