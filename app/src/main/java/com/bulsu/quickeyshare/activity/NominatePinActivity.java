package com.bulsu.quickeyshare.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.EZSharedPrefences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NominatePinActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.etPIN)
    EditText etPIN;
    @Bind(R.id.btnCancel)
    Button btnCancel;
    @Bind(R.id.btnOk)
    Button btnOk;

    @Bind(R.id.spQuestions)
    Spinner spQuestions;
    @Bind(R.id.etSecurityQuestion)
    EditText etSecurityQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nominate_pin);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("Security");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @OnClick({R.id.btnCancel, R.id.btnOk})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                onCancelClick();
                break;
            case R.id.btnOk:
                onOkClick();
                break;
        }
    }

    private void onCancelClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need set a pin before you use lock features");
        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void onOkClick() {

        String pin = etPIN.getText().toString().trim();
        String answer = etSecurityQuestion.getText().toString().trim();
        String question = spQuestions.getSelectedItem().toString();

        Log.d("TAG", question);

        if (pin.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Nominate a Pin", Toast.LENGTH_SHORT).show();
        } else if (answer.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please add answer to security question", Toast.LENGTH_SHORT).show();
        } else {

            EZSharedPrefences.setPinCode(this, pin);
            EZSharedPrefences.setSecAnswer(this, answer);
            EZSharedPrefences.setSecQuestion(this, question);

            startActivity(new Intent(getApplicationContext(), LockitActivity.class));
            finish();
        }

    }
}
