package com.bulsu.quickeyshare.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.EZSharedPrefences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PinActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.etPIN)
    EditText etPIN;
    @Bind(R.id.btnCancel)
    Button btnCancel;
    @Bind(R.id.btnOk)
    Button btnOk;
    @Bind(R.id.activity_nominate_pin)
    LinearLayout activityNominatePin;
    @Bind(R.id.tvForget)
    TextView tvForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        ButterKnife.bind(this);

        setTitle("Security");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void onOkClicked() {

        String tempPin = etPIN.getText().toString().trim();
        String savePin = EZSharedPrefences.getPinCode(this);

        if (tempPin.equals(savePin)) {
            startActivity(new Intent(this, LockitActivity.class));
            finish();
        } else {
            Toast.makeText(this, "You have entered an incorrect pin", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.tvForget, R.id.btnCancel, R.id.btnOk})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvForget:
                onForgetClick();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnOk:
                onOkClicked();
                break;
        }
    }

    private void onForgetClick() {

        final EditText et = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(EZSharedPrefences.getSecQuestion(this));
        builder.setView(et);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String secretAnswer = EZSharedPrefences.getSecAnswer(PinActivity.this);
                String tempAnswer = et.getText().toString();

                if (secretAnswer.equalsIgnoreCase(tempAnswer)){
                    startActivity(new Intent(PinActivity.this, NominatePinActivity.class));
                    dialogInterface.dismiss();
                }else{
                    Toast.makeText(PinActivity.this, "Your answer is incorrect", Toast.LENGTH_LONG).show();
                    et.setText("");
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }
}
