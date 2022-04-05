package com.example.rosariosisapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

public class MainActivity<Class1, Teacher1, Grade1> extends AppCompatActivity {

    /* Define the UI elements */
    private EditText eName;
    private EditText ePassword;
    private Button eLogin;
    private CheckBox check;
    public boolean isValid2=false;
    public boolean isValid=false;
//

    String userName = "";
    String userPassword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        /* Bind the XML views to Java Code Elements */
        eName = findViewById(R.id.etName);
        ePassword = findViewById(R.id.etPassword);
        eLogin = findViewById(R.id.btnLogin);
        check = findViewById(R.id.checkBox);

        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String checkbox = preferences.getString("remember","");
        if(checkbox.equals("true") && preferences.getString("username", "").length() > 0){
            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
            intent.putExtra("username", preferences.getString("username", ""));
            intent.putExtra("userpassword", preferences.getString("userpassword", ""));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this,"Please Sign In.", Toast.LENGTH_SHORT).show();
        }

        /* Describe the logic when the login button is clicked */
        eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Obtain user inputs */
                userName = eName.getText().toString();
                userPassword = ePassword.getText().toString();

                /* Check if the user inputs are empty */
                if(userName.isEmpty() || userPassword.isEmpty())
                {
                    /* Display a message toast to user to enter the details */
                    Toast.makeText(MainActivity.this, "Please enter name and password!", Toast.LENGTH_SHORT).show();
                } else {

                    /* Validate the user inputs */
                    login_webscrape lw = new login_webscrape(); //not sure if this part works
                    lw.execute();
                }
            }
        });

    }

    private class login_webscrape extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String mp = null;
                String yr = null;
                final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
                final String LOGIN_FORM_URL = "https://rosariosis.asianhope.org/index.php";
                //rather than the grades, the initial log in action url is the portral page possibly?
                final String GRADES_URL = "https://rosariosis.asianhope.org/Modules.php?modname=Grades/StudentGrades.php";

                Connection.Response loginForm = Jsoup.connect(LOGIN_FORM_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();

                loginForm = Jsoup.connect(LOGIN_FORM_URL)
                        .cookies(loginForm.cookies())
                        .data("USERNAME", userName)
                        .data("PASSWORD", userPassword)
                        .method(Connection.Method.POST)
                        .followRedirects(true)
                        .userAgent(USER_AGENT)
                        .execute();

                isValid2 = false;

                if(!(loginForm.url().toString().equals(LOGIN_FORM_URL))){
                    Log.d("logintest", "loginForm:" + loginForm.url().toString());
                    Log.d("logintest", "LOGIN_FORM_URL:" + LOGIN_FORM_URL);
                    isValid2=true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            isValid = validate(userName, userPassword);

            /* Validate the user inputs */

            /* If not valid */
            if (!isValid) { {
                Toast.makeText(MainActivity.this, "Incorrect credentials, please try again!", Toast.LENGTH_SHORT).show();
            }
            }
            /* If valid */
            else {
                if (check.isChecked()) {
                    SharedPreferences preferences1 = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences1.edit();
                    editor.putString("remember", "true");
                    editor.putString("username", userName);
                    editor.putString("userpassword", userPassword);
                    editor.commit();
                    //Toast.makeText(MainActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences preferences1 = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences1.edit();
                    editor.putString("remember", "false");
                    editor.commit();
                    //Toast.makeText(MainActivity.this, "Save Credentials?", Toast.LENGTH_SHORT).show();
                }

                /* Allow the user in to your app by going into the next activity */
                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                intent.putExtra("username", userName);
                intent.putExtra("userpassword", userPassword);
                startActivity(intent);
                finish();
            }
        }
    }

    /* Validate the credentials */
    private boolean validate(String userName, String userPassword)
    {

        if(isValid2){
            Log.d("logintest", "login is valid");
            return true;
        }
        else{
            Log.d("logintest", "login is invalid");
            return false;
        }


        /* Check the credentials */
        /* Get the object of Credentials class */
        //  Credentials credentials = new Credentials();
        /*
        if(userName.equals(credentials.name) && userPassword.equals(credentials.password))
        {
            return true;
        }
        return false;
         */


    }


}