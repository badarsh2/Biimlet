package example.com.miibletsample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.model.History;
import com.google.api.services.gmail.model.HistoryMessageAdded;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListHistoryResponse;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Gmail API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTaskGmail extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;
    List<EMail> s = new ArrayList<>();
    JSONArray st = new JSONArray();
    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTaskGmail(MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Gmail API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.clearResultsText();
            SharedPreferences sharedPreferences = mActivity.getSharedPreferences("MAILS"+mActivity.getPreferences(Context.MODE_PRIVATE).getString(MainActivity.PREF_ACCOUNT_NAME,""), 0);
            String his = sharedPreferences.getString("history", "");
            String mails = sharedPreferences.getString("mails", null);
            if(his.equals("")) {
                mActivity.updateResultsTextGmail(listMessagesMatchingQuery());
            }
            else {
                mActivity.updateResultsTextGmail(updateMessages(new BigInteger(his), mails));
            }

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainActivity.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }

        return null;
    }

    /**
     * Fetch a list of Gmail labels attached to the specified account.
     * @return List of Strings labels.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        List<String> labels = new ArrayList<String>();
        ListLabelsResponse listResponse =
                mActivity.mServicegmail.users().labels().list(user).execute();
        for (Label label : listResponse.getLabels()) {
            labels.add(label.getName()+":"+label.getId());
        }
        return labels;
    }

    public List<EMail> listMessagesMatchingQuery() throws IOException, JSONException {
        String userId = "me";
        List<String> lid = new ArrayList<>();
        lid.add("INBOX");
        ListMessagesResponse response = mActivity.mServicegmail.users().messages().list(userId).setLabelIds(lid).execute();

        List<Message> messages = new ArrayList<Message>();
        //while (response.getMessages() != null) {
        messages.addAll(response.getMessages());
            /*if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = mActivity.mService.users().messages().list(userId)
                        .setMaxResults((long)5)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }*/
        //}


        BatchRequest b = mActivity.mService.batch();

        JsonBatchCallback<Message> bc = new JsonBatchCallback<Message>() {

            @Override
            public void onSuccess(Message mes, HttpHeaders responseHeaders)
                    throws IOException {
                if(s.size()==0){
                    SharedPreferences sharedpreferences = mActivity.getSharedPreferences("MAILS"+mActivity.getPreferences(Context.MODE_PRIVATE).getString(MainActivity.PREF_ACCOUNT_NAME,""), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("history",mes.getHistoryId()+"");
                    editor.commit();
                }
                List<MessagePartHeader> m = mes.getPayload().getHeaders();
                JSONArray a = new JSONArray(m);
                String From="",Subject="",Snippet=mes.getSnippet();
                for(int i=0;i<m.size();i++) {
                    if (m.get(i).getName().equals("From"))
                        From = m.get(i).getValue().split("<")[0];
                    else if (m.get(i).getName().equals("Subject"))
                        Subject = m.get(i).getValue();
                }
                EMail e = new EMail(From, Subject, Snippet, mes.getId());
                s.add(e);
                try {
                    st.put(new JSONObject(e.makeStr()));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                    throws IOException {

            }
        };


        for (Message message : messages) {
            mActivity.mServicegmail.users().messages().get(userId, message.getId()).setFormat("metadata").queue(b,bc);

        }
        b.execute();
        SharedPreferences sharedpreferences = mActivity.getSharedPreferences("MAILS"+mActivity.getPreferences(Context.MODE_PRIVATE).getString(MainActivity.PREF_ACCOUNT_NAME,""), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("mails", st.toString());
        editor.commit();
        return s;
    }

    public List<EMail> updateMessages( BigInteger startHistoryId , String mails)
            throws IOException, JSONException {
        String userId="me";
        List<History> histories = new ArrayList<History>();
        ListHistoryResponse response = mActivity.mServicegmail.users().history().list(userId).setStartHistoryId(startHistoryId).setLabelId("INBOX").execute();
        //while (response.getHistory() != null) {
        if(response.size()!=1)
            histories.addAll(response.getHistory());
            /*if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = mActivity.mService.users().history().list("me").setPageToken(pageToken)
                        .setStartHistoryId(startHistoryId).execute();
            } else {
                break;
            }
        }*/
        BatchRequest b = mActivity.mService.batch();

        JsonBatchCallback<Message> bc = new JsonBatchCallback<Message>() {

            @Override
            public void onSuccess(Message mes, HttpHeaders responseHeaders)
                    throws IOException {
                if(s.size()==0){
                    SharedPreferences sharedpreferences = mActivity.getSharedPreferences("MAILS"+mActivity.getPreferences(Context.MODE_PRIVATE).getString(MainActivity.PREF_ACCOUNT_NAME,""), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("history",mes.getHistoryId()+"");
                    editor.commit();
                }
                List<MessagePartHeader> m = mes.getPayload().getHeaders();
                JSONArray a = new JSONArray(m);
                String From="",Subject="",Snippet=mes.getSnippet();
                for(int i=0;i<m.size();i++) {
                    if (m.get(i).getName().equals("From"))
                        From = m.get(i).getValue().split("<")[0];
                    else if (m.get(i).getName().equals("Subject"))
                        Subject = m.get(i).getValue();
                }
                EMail e = new EMail(From, Subject, Snippet, mes.getId());
                s.add(e);
                try {
                    st.put(new JSONObject(e.makeStr()));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                    throws IOException {

            }
        };
        int bat=0;
        if(histories.size()!=0)
        for (History history:histories) {
            List<HistoryMessageAdded> messages = history.getMessagesAdded();
            if(messages!=null)
            for (HistoryMessageAdded m:messages){
                mActivity.mServicegmail.users().messages().get(userId, m.getMessage().getId()).setFormat("metadata").queue(b,bc);
                bat++;
            }
        }
        if(bat!=0)
        b.execute();
        JSONArray mailjson = new JSONArray(mails);
        for (int i=0;i<mailjson.length()-bat;i++){
            JSONObject obj = mailjson.getJSONObject(i);
            EMail eMail = new EMail(obj.getString("From"),obj.getString("Subject"),obj.getString("Snippet"),obj.getString("Id"));
            s.add(eMail);
            st.put(obj);
        }
        SharedPreferences sharedpreferences = mActivity.getSharedPreferences("MAILS"+mActivity.getPreferences(Context.MODE_PRIVATE).getString(MainActivity.PREF_ACCOUNT_NAME,""), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("mails", st.toString());
        editor.commit();
        return s;
    }

}
