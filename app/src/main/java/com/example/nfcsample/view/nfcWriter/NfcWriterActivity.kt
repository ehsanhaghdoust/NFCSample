package com.example.nfcsample.view.nfcWriter

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcsample.databinding.ActivityNfcWriterBinding
import java.util.Locale

class NfcWriterActivity: AppCompatActivity() {

    lateinit var binding: ActivityNfcWriterBinding
    val viewModel: NfcWriterViewModel by viewModels()
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNfcWriterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

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


    private fun enableForegroundDispatch() {
        val intent = Intent(this, javaClass).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            try {
                addDataScheme("text/plain") // Add data MIME type for text
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                e.printStackTrace()
            }
        })
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    private fun disableForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
            writeTextToNfcTag(tag)
        }
    }

    private fun writeTextToNfcTag(tag: Tag) {
        val text = "Ehsan Haghdoust" // Replace with your desired text

        val ndefRecord = createTextRecord(text)
        val ndefMessage = NdefMessage(arrayOf(ndefRecord))

        // Check if tag is writable
//        if (!tag.isWritable) {
//            Toast.makeText(this, "This tag is not writable.", Toast.LENGTH_SHORT).show()
//            return
//        }

        // Perform NDEF write operation

//        with(NfcTag.from(tag)) {
        with(Ndef.get(tag)) {
            try {
                connect()
                writeNdefMessage(ndefMessage)
                Toast.makeText(this@NfcWriterActivity, "Text written to NFC tag!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@NfcWriterActivity, "Failed to write to NFC tag!", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                close()
            }
        }
    }

    private fun createTextRecord(message: String): NdefRecord {
        val langCode = Locale.ENGLISH.language
        val textBytes = message.toByteArray(Charsets.UTF_8)
        val langBytes = langCode.toByteArray(Charsets.UTF_8)
        val langLength = langBytes.size
        val textLength = textBytes.size

        val payload = ByteArray(1 + langLength + textLength)
//        val aa = 0x1F
        payload[0] = (langLength and 0x1F).toByte() // Text encoding
        payload[1] = langCode.toByte() // Language code
        System.arraycopy(textBytes, 0, payload, 2, textLength)

        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
    }
}