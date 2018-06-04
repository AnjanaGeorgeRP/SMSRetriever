package com.myapplicationdev.android.smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v4.content.res.TypedArrayUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecond extends Fragment {

    EditText etFrag2;
    Button btFrag2;
    TextView tvFrag2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_second, container, false);
        etFrag2 = (EditText) view.findViewById(R.id.etFrag2);
        btFrag2 = (Button) view.findViewById(R.id.btnFrag2);
        tvFrag2 = (TextView) view.findViewById(R.id.tvFrag2);

        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg);

        btFrag2.setOnClickListener(new View.OnClickListener() {
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

                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                ContentResolver cr = getActivity().getContentResolver();

                //Get the word to filter
                int selectedButtonId = rg.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) view.findViewById(selectedButtonId);

                String[] str;
                ArrayList<String> wordArray  = new ArrayList<>();
                if (rb.getText().equals("Phrase")) {
                    wordArray.add(etFrag2.getText().toString().trim());
                    Log.e("here", "here");
                } else {
                    str = etFrag2.getText().toString().trim().split(" ");
                    wordArray.addAll(Arrays.asList(str));
                    for(int i = 0; i < str.length;i++){
                        if(str[i].length() == 0){
                            wordArray.remove(str[i]);
                        }
                    }
                }

                String filter = "";
                String filterString = "";

                String[] filterArgs = new String[wordArray.size()];

                for (int i = 0; i < wordArray.size(); i++) {
                    if (i != 0) {
                        filter += " OR ";
                    }
                    filter += "body LIKE ?";
                        filterString = "%" + wordArray.get(i) + "%";
                        // The matches for the ?
                        filterArgs[i] = filterString;
                        // Fetch SMS Message from Built-in Content Provider
                }


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
                tvFrag2.setText(smsBody);
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btFrag2.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getContext(), "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

