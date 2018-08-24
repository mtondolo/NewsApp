package int_.comesa.www.comesanews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mNewsListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mNewsListTextView = (TextView) findViewById(R.id.tv_news);

        /*
         * This String array contains news headlines.
         *
         */
        String[] news = News.getNews();

        /*
         * Iterate through the array and append the Strings to the TextView. The purpose of
         * the "\n\n\n" after the String is to give visual separation between each String in the
         * TextView.
         */
        for (String newHeadline : news) {
            mNewsListTextView.append(newHeadline + "\n\n\n");
        }
    }
}
