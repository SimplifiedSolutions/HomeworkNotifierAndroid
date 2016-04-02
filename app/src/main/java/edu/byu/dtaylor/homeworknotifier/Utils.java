package edu.byu.dtaylor.homeworknotifier;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.byu.dtaylor.homeworknotifier.gsontools.GsonDatabase;

/**
 * Created by dtaylor on 3/26/2016.
 */
public class Utils {
    public static GsonDatabase getAllInfo(final Context context, String netID, String password)
    {
        HttpResponse response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost();
            post.setURI(new URI("http://ec2-54-187-234-170.us-west-2.compute.amazonaws.com:3000/GetAllInfo"));

            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("netID", netID));
            postParameters.add(new BasicNameValuePair("password", password));

            post.setEntity(new UrlEncodedFormEntity(postParameters));
            response = client.execute(post);
            String res = EntityUtils.toString(response.getEntity());
            GsonDatabase database = new Gson().fromJson(res,GsonDatabase.class);
            return database;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String stringifyDate(Calendar calendar, boolean displayYear)
    {
        calendar.setTime(new Date(calendar.getTime().getTime() * 1000));
        String dayNumberSuffix = getDayNumberSuffix(calendar.get(Calendar.DAY_OF_MONTH));
        String year = "";
        if (displayYear) year = " yyyy" ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d'" + dayNumberSuffix + "'" + year);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * converts
     * @param date
     * @param displayYear
     * @return
     */
    public static String stringifyDate(Date date, boolean displayYear)
    {
        date = new Date(date.getTime() * 1000);
        String year = "";
        if (displayYear) year = " yyyy" ;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String dayNumberSuffix = getDayNumberSuffix(calendar.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d'" + dayNumberSuffix + "'" + year);
        return dateFormat.format(calendar.getTime());
    }


    public static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

}
class CustomAsyncTask extends AsyncTask<Object,Object,Object>
{
    CustomTaskListener listener;
    CustomAsyncTask(CustomTaskListener listener)
    {
        this.listener = listener;
    }
    @Override
    protected Object doInBackground(Object... params) {
        return listener.doInBackground(params);
    }

    @Override
    protected void onPreExecute() {
        listener.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        listener.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        listener.onProgressUpdate(values);
    }
}
interface CustomTaskListener {
    void onPostExecute(Object object);
    Object doInBackground(Object[] params);
    void onPreExecute();
    void onProgressUpdate(Object[] values);
}
