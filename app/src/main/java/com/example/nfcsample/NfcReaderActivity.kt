package com.example.nfcsample

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcsample.databinding.ActivityNfcReaderBinding
import java.io.IOException

class NfcReaderActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var binding: ActivityNfcReaderBinding

    private lateinit var mNfcAdapter: NfcAdapter
    private lateinit var mUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNfcReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!mNfcAdapter.isEnabled) {
            Toast.makeText(this, "Activate NFC feature", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onTagDiscovered(tag: Tag) {

        val mNdef = Ndef.get(tag);
        if (mNdef != null) {
            val mRecord = NdefRecord.createUri(mUrl);
            val mNdefMsg = NdefMessage(mRecord)
            try {
                mNdef.connect();
                mNdef.writeNdefMessage(mNdefMsg)
                runOnUiThread {
                    Toast.makeText(this, "NFC tag detected", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    mNdef.close();
                } catch (e: IOException) {
                    Toast.makeText(this, "IO error happened", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Invalid tag", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val options = Bundle()
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1024)
        mNfcAdapter.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NFC_BARCODE,
            options
        )
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter.disableReaderMode(this);
    }
}