package com.example.nfcsample.view.nfcWriter

import android.app.Application
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import androidx.lifecycle.AndroidViewModel
import java.io.IOException

class NfcWriterViewModel(application: Application): AndroidViewModel(application){

    fun writeOnTag(message: String) {
        val intent = Intent()
        handleNfcIntent(intent, message)
    }

    fun createNdefMessage(text: String): NdefMessage? {
        val mimeType = "text/plain"
        val payload = text.toByteArray(Charsets.UTF_8)
        val record = NdefRecord(NdefRecord.TNF_WELL_KNOWN, mimeType.toByteArray(Charsets.UTF_8), ByteArray(0), payload)
        return NdefMessage(arrayOf(record))
    }

    // This function attempts to write the NDEF message to the tag
    fun writeNdefToTag(ndefMessage: NdefMessage?, tag: Tag): Boolean {
        if (ndefMessage == null) {
            return false
        }
        val ndef = Ndef.get(tag)
        return try {
            ndef.connect()
            ndef.writeNdefMessage(ndefMessage)
            true
        } catch (e: Exception) {
            false
        } finally {
            ndef.close()
        }
    }

    // This function is typically called in an Activity's onNewIntent method
//  when an NFC tag is discovered
    fun handleNfcIntent(intent: Intent, textToWrite: String): Boolean {
        val action = intent.action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return false
            val ndefMessage = createNdefMessage(textToWrite)
            return writeNdefToTag(ndefMessage, tag)
        }
        return false
    }

//    fun createNFCMessage(payload: String, intent: Intent?): Boolean {
//
//        val pathPrefix = "peterjohnwelcome.com:nfcapp"
//        val nfcRecord = NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, pathPrefix.toByteArray(), ByteArray(0), payload.toByteArray())
//        val nfcMessage = NdefMessage(arrayOf(nfcRecord))
//        intent?.let {
//            val tag = it.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
//            return writeMessageToTag(nfcMessage, tag)
//        }
//        return false
//    }
//
//    private fun writeMessageToTag(nfcMessage: NdefMessage, tag: Tag?): Boolean {
//
//        try {
//            val nDefTag = Ndef.get(tag)
//
//            nDefTag?.let {
//                it.connect()
//                if (it.maxSize < nfcMessage.toByteArray().size) {
//                    //Message to large to write to NFC tag
//                    return false
//                }
//                if (it.isWritable) {
//                    it.writeNdefMessage(nfcMessage)
//                    it.close()
//                    //Message is written to tag
//                    return true
//                } else {
//                    //NFC tag is read-only
//                    return false
//                }
//            }
//
//            val nDefFormatableTag = NdefFormatable.get(tag)
//
//            nDefFormatableTag?.let {
//                try {
//                    it.connect()
//                    it.format(nfcMessage)
//                    it.close()
//                    //The data is written to the tag
//                    return true
//                } catch (e: IOException) {
//                    //Failed to format tag
//                    return false
//                }
//            }
//            //NDEF is not supported
//            return false
//
//        } catch (e: Exception) {
//            //Write operation has failed
//        }
//        return false
//    }

}