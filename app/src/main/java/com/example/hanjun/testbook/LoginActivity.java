package com.example.hanjun.testbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {
    EditText text1;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("share", MODE_PRIVATE);
        setContentView(R.layout.activity_login);
        text1 = (EditText) findViewById(R.id.editText1);
        Button button1 = (Button) findViewById(R.id.button1);
        MyClickListener myClickListener = new MyClickListener();
        button1.setOnClickListener(myClickListener);
    }

    class MyClickListener implements View.OnClickListener {
        public void onClick(View v) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user", text1.getText().toString());
            editor.commit();
            Intent intent = new Intent(LoginActivity.this,CommunityActivity.class);
            startActivity(intent);
            finish();

        }
    }
}
