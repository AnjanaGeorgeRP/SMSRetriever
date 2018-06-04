package com.myapplicationdev.android.smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFirst extends Fragment {

    EditText etFrag1;
    Button btFrag1;
    TextView tvFrag1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_first, container, false);

        etFrag1 = (EditText) view.findViewById(R.id.etFrag1);
        btFrag1 = (Button) view.findViewById(R.id.btnFrag1);
        tvFrag1 = (TextView) view.findViewById(R.id.tvFrag1);


        btFrag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //permission
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getContext(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }

                Uri uri = Uri.parse("content://sms/inbox");
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                ContentResolver cr = getActivity().getContentResolver();

                String str = etFrag1.getText().toString().trim();

                // The filter String
                String filter = "address LIKE ?";
                //Get the word to filter

                String filterString = "%"+str+"%";
                // The matches for the ?
                String[] filterArgs = {filterString};
                // Fetch SMS Message from Built-in Content Provider

                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvFrag1.setText(smsBody);
            }
        });

        return view;
    }
}

