package com.example.nfcsample.view.nfcReader

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcsample.databinding.ActivityNfcReaderBinding
import com.example.nfcsample.model.NFCManager
import com.example.nfcsample.model.NFCStatus
import com.example.nfcsample.utils.Coroutines
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

class NfcReaderActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    companion object {
        private val TAG = NfcReaderActivity::class.java.getSimpleName()
    }

    private lateinit var binding: ActivityNfcReaderBinding
    private val viewModel : NfcReaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNfcReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Coroutines.main(this@NfcReaderActivity) { scope ->
            scope.launch(block = {
                viewModel.observeNFCStatus().collectLatest(action = { status ->
                    Log.d(TAG, "observeNFCStatus $status")
                    if (status == NFCStatus.NoOperation) NFCManager.disableReaderMode(this@NfcReaderActivity, this@NfcReaderActivity)
                    else if (status == NFCStatus.Tap) NFCManager.enableReaderMode(this@NfcReaderActivity,
                                                                                  this@NfcReaderActivity,
                                                                                  this@NfcReaderActivity,
                                                                                  viewModel.getNFCFlags(),
                                                                                  viewModel.getExtras())
                })
            })
            scope.launch(block = {
                viewModel.observeToast().collectLatest(action = { message ->
                    Log.d(TAG, "observeToast $message")
                    Toast.makeText(this@NfcReaderActivity, message, Toast.LENGTH_LONG).show()
                })
            })

            scope.launch( block = {
                viewModel.observeTag().collectLatest (action = { tag -> Log.d(TAG, "observeTag $tag")
                    binding.textView.text = tag
//                    binder?.textViewExplanation?.setText(tag)
                })
            })

            viewModel.onCheckNFC(true)
        }
    }


    override fun onTagDiscovered(tag : Tag?) {
        viewModel.readTag(tag)
    }
}