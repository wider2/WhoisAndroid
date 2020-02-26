package onboard.com.whois;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    TextView tvOutput;
    private static Pattern pattern;
    private Matcher matcher;
    private EditText etHostname;
    private Button btSearch;

    //String WHOIS_SERVER = "whois.iana.org";
    String WHOIS_SERVER = "whois.internic.net";
    int WHOIS_PORT = 43;


    // regex whois parser
    private static final String WHOIS_SERVER_PATTERN = "Whois Server:\\s(.*)";

    static {
        pattern = Pattern.compile(WHOIS_SERVER_PATTERN);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutput = (TextView) findViewById(R.id.tv_output);
        etHostname = (EditText) findViewById(R.id.et_hostname);
        btSearch = (Button) findViewById(R.id.bt_search);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAsyncTask().execute(etHostname.getText().toString());
            }
        });

        //new MyAsyncTask().execute();
    }


    private class MyAsyncTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            String rslt = "", hostname = "";
            try {
                hostname = urls[0];
                rslt = getWhois(hostname);

                return rslt;
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }

        protected void onPostExecute(String result) {
            tvOutput.setText("Result: " + result);
        }
    }


    public String getWhois(String request) {
        String result = "";
        Socket theSocket;

        try {
            theSocket = new Socket(WHOIS_SERVER, WHOIS_PORT, true);
            Writer out = new OutputStreamWriter(theSocket.getOutputStream());
            out.write(request + "\r\n");
            out.flush();
            DataInputStream theWhoisStream;
            theWhoisStream = new DataInputStream(theSocket.getInputStream());
            String s;
            while ((s = theWhoisStream.readLine()) != null) {
                result = result + s + "\n";
            }
        } catch (IOException e) {
            result = e.getMessage() + "\n";
        }

        return result;
    }

}
