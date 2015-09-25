package example.com.miibletsample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * An asynchronous task that handles the Gmail API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTaskGmail extends AsyncTask<Void, Void, Void> {
    private FragmentGmail mActivity;

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTaskGmail(FragmentGmail activity) {
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
            /*SharedPreferences sharedPreferences = mActivity.getActivity().getSharedPreferences("MAILS",0);
            String s = sharedPreferences.getString("history", 0 + "");*/
            /*if(s.equals("0"))*/
            mActivity.updateResultsText(listMessagesMatchingQuery());
            /*else
                mActivity.updateResultsText(updateMessages());*/

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
        if (mActivity.mProgress.isShowing()) {
            mActivity.mProgress.dismiss();
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
                mActivity.mService.users().labels().list(user).execute();
        for (Label label : listResponse.getLabels()) {
            labels.add(label.getName()+":"+label.getId());
        }
        return labels;
    }

    public List<EMail> listMessagesMatchingQuery() throws IOException, JSONException {
        String userId = "me",query="";
        List<String> lid = new ArrayList<>();
        lid.add("INBOX");
        ListMessagesResponse response = mActivity.mService.users().messages().list(userId).setLabelIds(lid).execute();

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
        List<EMail> s = new ArrayList<>();
        ArrayList<String> st = new ArrayList<>();

        for (Message message : messages) {
            Message mes = mActivity.mService.users().messages().get(userId, message.getId()).setFormat("metadata").execute();
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
            st.add(e.makeStr());
        }
        SharedPreferences sharedpreferences = mActivity.getActivity().getSharedPreferences("MAILS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putStringSet("mails",new HashSet<String>(st));
        editor.putString("history",messages.get(0).getHistoryId()+"");
        editor.commit();
        return s;
    }

    /*public static void listHistory(Gmail service, String userId, BigInteger startHistoryId)
            throws IOException {
        List<History> histories = new ArrayList<History>();
        ListHistoryResponse response = service.users().history().list(userId)
                .setStartHistoryId(startHistoryId).execute();
        //while (response.getHistory() != null) {
            histories.addAll(response.getHistory());
            *//*if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().history().list(userId).setPageToken(pageToken)
                        .setStartHistoryId(startHistoryId).execute();
            } else {
                break;
            }
        }*//*
        for (History history : histories) {
            List<HistoryMessageAdded> messages = history.getMessagesAdded();
            for (HistoryMessageAdded m:messages){
                Message message = m.getMessage();
            }
        }
    }*/

}
