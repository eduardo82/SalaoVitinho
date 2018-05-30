package br.com.eduardo.salaovitinho.util;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Eduardo on 12/02/2018.
 */

public class ContactsUtil {

    public static boolean contatoEstaSalvoAgenda(Context context, String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                String nome = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                return nome != null;
            }
        }
        finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }
        return false;
    }

    public static final boolean salvaContatoAgenda(Context context, String telefone, String nome) {
        try {
            ArrayList<ContentProviderOperation> operacao = new ArrayList<>();

            operacao.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nome).build());

            operacao.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, telefone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());

            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operacao);
        } catch (Exception e) {
            Toast.makeText(context, "Não foi possível salvar o contato " + nome, Toast.LENGTH_LONG).show();
        }

        Toast.makeText(context, "Contato " + nome + " salvo!", Toast.LENGTH_LONG).show();
        return true;
    }
}
