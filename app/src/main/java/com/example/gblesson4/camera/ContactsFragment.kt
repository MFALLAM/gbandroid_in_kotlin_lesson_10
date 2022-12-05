package com.example.gblesson4.camera

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.jar.Manifest

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding

    companion object {
        fun newInstance() = ContactsFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(checkPermission(
                Manifest.permission.READ_CONTACTS,
                "Доступ к контактам",
                "Разрешение требуется для отображения контактов"
            )) getContacts()
    }

    private fun checkPermission(permission: String, title: String, message: String): Boolean {
        val permResult =
            ContextCompat.checkSelfPermission(requireContext(), permission)
        if (shouldShowRequestPermissionRationale(permission)) {
            AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Предоставить доступ") { _, _ ->
                    permissionRequest(permission)
                }
                .setNegativeButton("Не надо") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        } else if (permResult != PackageManager.PERMISSION_GRANTED) {
            permissionRequest(permission)
        } else {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_READ_CONTACTS
            && grantResults[permissions.indexOf(Manifest.permission.READ_CONTACTS)]
            == PackageManager.PERMISSION_GRANTED
        ) {
            getContacts()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun permissionRequest(permission: String) {
        requestPermissions(arrayOf(permission), REQUEST_CODE_READ_CONTACTS)
    }

    private fun getContacts() {
        val contextResolver = requireContext().contentResolver
        val cursorWithContacts: Cursor? = contextResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursorWithContacts?.let { contactsCursor ->
            for (idx in 0 until contactsCursor.count) {
                contactsCursor.moveToPosition(idx)

                val indexName = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val contactName = contactsCursor.getString(indexName)
                val indexID = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
                val contactID = contactsCursor.getString(indexID)
                val contactNumber = getNumberFromID(contextResolver, contactID)
                addView(contactName, contactNumber)
            }
        }
        cursorWithContacts?.close()
    }

    private fun getNumberFromID(cr: ContentResolver, contactId: String): String {
        val phones = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null
        )
        var number: String = "none"
        phones?.let { cursor ->
            while (cursor.moveToNext()) {
                val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                number = cursor.getString(phoneIndex)
            }
        }
        return number
    }

    @SuppressLint("SetTextI18n")
    private fun addView(name: String, number: String) {
        with(binding.containerForContacts) {
            addView(AppCompatTextView(requireContext()).apply {
                text = "$name -> $number"
                textSize = 16F
            })
        }
    }
}