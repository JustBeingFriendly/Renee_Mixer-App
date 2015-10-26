package com.example.stephen.renee;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.graphics.Color;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button redButton; // To allow reference for inner class
    final String connectionString = "http://192.168.0.107:8081/hello"; //LAN address for web server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change properties of the layout
        RelativeLayout aLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams buttonDetails = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        // Set global button properties
        buttonDetails.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonDetails.addRule(RelativeLayout.CENTER_VERTICAL);

        // Create and set the redButton
        redButton = new Button(this);
        redButton.setText("click it, click it good ");
        redButton.setBackgroundColor(Color.RED);

        // Button to layout
        aLayout.addView(redButton, buttonDetails);
        setContentView(aLayout);

        // Add an action listener
        redButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        ConnectToServer cts = new ConnectToServer();
                        cts.execute(connectionString); //Pass the connection string to the async class/method
                    }
        });
    }

    //Inner class for asynchronous call
    //Doing networking stuff in the main thread can cause the application to hang
    //AsyncTask creates a second thread to process networking stuff through
    private class ConnectToServer extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... theURL){ //theURL is the parameter connectionString from cts.execute
            String strToReturn= "";
            try {
                URL url = new URL(theURL[0]); //the method is expecting an array, as there is only one parameter passed we can set it to the first entry in the array 0
                HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

                try{
                    InputStream in = new BufferedInputStream(urlConnect.getInputStream()); //This is when the connection to the web server actually executes
                    StringBuilder sb = new StringBuilder(); //For reconstructing the data returned from the input stream, stringbuilder is used as it is memory efficient
                    InputStreamReader inStrRead = new InputStreamReader(in); //For reading the input from the input stream
                    //Perform the reconstruction of input data into the stringbuilder
                    int data = inStrRead.read();
                    while(data != -1){
                        char current = (char) data;
                        data = inStrRead.read();
                        sb.append(current);
                    }
                    strToReturn = sb.toString(); //Cast the stringbuilder to a string
                    in.close(); //Close the InputStream
                }
                catch (Exception e){
                    Log.e("DAMMIT", "WE has an error", e);
                }
                finally {
                    urlConnect.disconnect(); //Disconnect from web server
                }
            }
            catch (Exception e){
                Log.e("Outer Tried", "WE has an error", e);
            }
            return strToReturn;
        }

        //Once the asynchronous call has finished change the text on the button
        @Override
        protected void onPostExecute(String result){
            if (!result.isEmpty()){
                redButton.setText(result); //Sets the button text to that returned from the web server
            }
            else{
                redButton.setText("Error Loading Webpage"); //Sets the button text to an error message
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
