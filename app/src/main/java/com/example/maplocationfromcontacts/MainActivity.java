package com.example.maplocationfromcontacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String DATA_MIMETYPE = ContactsContract.Data.MIMETYPE;
    private static final Uri DATA_CONTENT_URI = ContactsContract.Data.CONTENT_URI;
    private static final String DATA_CONTACT_ID = ContactsContract.Data.CONTACT_ID;
    private static final String CONTACTS_ID = ContactsContract.Contacts._ID;
    private static final Uri CONTACTS_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    private static final String STRUCTURED_POSTAL_CONTENT_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;
    private static final String STRUCTURED_POSTAL_FORMATTED_ADDRESS = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;
    private static final int PICK_CONTACT_REQUEST = 0;
    static String TAG = "MapLocation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Gán chức năng cho nút "Mở bản đồ"
        final Button button = findViewById(R.id.mapButton);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Tạo Intent để mở danh bạ và chọn một liên hệ
                    Intent intent = new Intent(Intent.ACTION_PICK, CONTACTS_CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi mở danh bạ: " + e.toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(data.getData(), null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_ID));
                String where = DATA_CONTACT_ID + " = ? AND " + DATA_MIMETYPE + " = ?";
                String[] whereParameters = new String[]{id, STRUCTURED_POSTAL_CONTENT_ITEM_TYPE};
                Cursor addrCur = cr.query(DATA_CONTENT_URI, null, where, whereParameters, null);

                if (addrCur != null && addrCur.moveToFirst()) {
                    String formattedAddress = addrCur.getString(addrCur.getColumnIndexOrThrow(STRUCTURED_POSTAL_FORMATTED_ADDRESS));

                    if (formattedAddress != null) {
                        formattedAddress = formattedAddress.replace(' ', '+');
                        Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + formattedAddress));
                        startActivity(geoIntent);
                    }
                }
                if (addrCur != null) {
                    addrCur.close();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "The activity is about to be restarted.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "The activity is about to become visible.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "The activity has become visible (it is now \"resumed\")");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Another activity is taking focus (this activity is about to be \"paused\")");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "The activity is no longer visible (it is now \"stopped\")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "The activity is about to be destroyed.");
    }
}
