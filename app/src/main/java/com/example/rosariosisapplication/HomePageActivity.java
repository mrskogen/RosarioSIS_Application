package com.example.rosariosisapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketException;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity {

    TableLayout classes;

    //NOTE: password is a RosarioSis password stored in strings.xml. DO NOT OPEN STRINGS.XML!
    String password;
    public ArrayList<ArrayList<String>> grades = new ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> classGrades = new ArrayList<ArrayList<String>>();
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
    final String LOGIN_FORM_URL = "https://rosariosis.asianhope.org/index.php";
    //rather than the grades, the initial log in action url is the portral page possibly?
    final String LOGIN_ACTION_URL = "https://rosariosis.asianhope.org/Modules.php?modname=misc/Portal.php";
    final String GRADES_URL = "https://rosariosis.asianhope.org/Modules.php?modname=Grades/StudentGrades.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        password = getResources().getString(R.string.andy_password);
        password = getString(R.string.andy_password);

        classes = (TableLayout) findViewById(R.id.main);

        description_webscrape dw = new description_webscrape();
        dw.execute();

    }

    private class description_webscrape extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Connection.Response loginForm = Jsoup.connect(LOGIN_FORM_URL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();

                loginForm = Jsoup.connect(LOGIN_FORM_URL)
                        .cookies(loginForm.cookies())
                        .data("USERNAME", "adosan")
                        .data("PASSWORD", password)
                        .method(Connection.Method.POST)
                        .followRedirects(true)
                        .userAgent(USER_AGENT)
                        .execute();

                Connection.Response quarter = Jsoup.connect("https://rosariosis.asianhope.org/Side.php?sidefunc=update")
                        .cookies(loginForm.cookies())
                        .data("syear", "2021")
                        .data("mp", "73")
                        .method(Connection.Method.POST)
                        .followRedirects(true)
                        .userAgent(USER_AGENT)
                        .execute();

                Document doc = Jsoup.connect(GRADES_URL)
                        .cookies(loginForm.cookies())
                        .userAgent(USER_AGENT)
                        .get();

                org.jsoup.nodes.Element table = doc.select("table[class=list widefat rt]").get(0);
                Elements rows = table.select("tr");

                for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                    org.jsoup.nodes.Element row = rows.get(i);
                    Elements cols = row.select("td");
                    Elements link = row.select("a[href]");
                    String classLink = "https://rosariosis.asianhope.org/" + link.get(0).attr("href");

                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(cols.get(0).text()); //Class name
                    temp.add(cols.get(1).text()); //Teacher name
                    temp.add(cols.get(3).text()); //Grade (percent)

                    temp.add(classLink);
                    grades.add(temp);

                    //NEW TEST CODE

                    org.jsoup.nodes.Document doc1 = Jsoup.connect(classLink)
                            .cookies(loginForm.cookies())
                            .userAgent(USER_AGENT)
                            .get();

                    org.jsoup.nodes.Element table1 = doc1.select("table[class=list widefat rt]").get(0);
                    doc1.select(".header2").remove();
                    doc1.select(".header2 align-right").remove();

                    Elements rows1 = table1.select("tr");
                    for (int j = 1; j < rows1.size(); j++) {

                        Element row1 = rows1.get(j);
                        Elements cols1 = row1.select("td");

                        ArrayList<String> temp1 = new ArrayList<String>();

                        if (cols1.size() > 0) {
                            temp1.add(cols.get(0).text());
                            temp1.add(cols1.get(0).text()); //Assignment Name
                            temp1.add(cols1.get(1).text()); //Assignment Category
                            temp1.add(cols1.get(2).text()); //Points / Possible
                            temp1.add(cols1.get(3).text()); //Grade (percent)

                            //Comment
                            if (cols1.get(4).text().length() > 0) {
                                temp1.add(cols1.get(4).text());
                            } else {
                                temp1.add ("No comment");
                            }
                        }

                        classGrades.add(temp1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //TESTING PURPOSES, PLS DONT DELETE YET
            //TODO: have class name in classGrades
            for (int i = 0; i< classGrades.size();i++){
                for (int j = 0; j< classGrades.get(i).size();j++){
                    Log.d("classgrades", classGrades.get(i).get(j));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            classes.removeViews(1, Math.max(0, classes.getChildCount() - 1));

            for (int i = 0; i < grades.size(); i++) {
                TableRow tbrow0 = new TableRow(HomePageActivity.this);
                tbrow0.setMinimumHeight(200);

                TextView tv0 = new TextView(HomePageActivity.this);
                TextView tv1 = new TextView(HomePageActivity.this);
                TextView tv2 = new TextView(HomePageActivity.this);
                tv0.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                tv1.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                tv2.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

                tv0.setGravity(Gravity.CENTER);
                tv1.setGravity(Gravity.CENTER);
                tv2.setGravity(Gravity.CENTER);

                tv0.setWidth(1500);
                tv1.setWidth(1500);
                tv2.setWidth(1500);

                tv0.setText(grades.get(i).get(0));
                tv1.setText(grades.get(i).get(1));
                tv2.setText(grades.get(i).get(2));

                tbrow0.addView(tv0);
                tbrow0.addView(tv1);
                tbrow0.addView(tv2);

                classes.addView(tbrow0);
            }
        }
    }
}