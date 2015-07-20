package org.lightsys.crmapp.CleanRepository.LoggedInAccount;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lightsys.crmapp.CleanModels.LoggedInUser;
import org.lightsys.crmapp.CleanModels.UserIdentifier;
import org.lightsys.crmapp.data.ErrorType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Jake on 7/16/2015.
 */
public class RESTApiDataSource implements LoggedInAccountSource {
    /**
     * We could potentially use a RestAPI class here to make it easy to reuse the REST Api between different repositories.
     * It's one more level of abstraction, but I'm not implementing it yet.
     *
     * I would like to use the Retrofit library from Square to implement the API portion, however,
     * Kardia's JSON API currently returns non-standard JSON that does not appear to be compatible
     * with Retrofit. (Kardia returns a collection as a single object rather than a JSON array)
     * While it might be possible to fix that with some custom code for Retrofit, for the time being
     * it's easier to use the code from the DataConnection class.
     */

    private String Host;
    private String Base_Host_Name;
    private String PORT = "800";
    private String apiEndpoint;
    private String collectionQueryOptions = "?cx__mode=rest&cx__res_type=collection&cx__res_format=attrs&cx__res_attrs=basic";
    private String elementQueryOptions = "?cx__mode=rest&cx__res_type=element&cx__res_format=attrs&cx__res_attrs=basic";
    private LoggedInUser loggedInUser;
    ErrorType errorType = null;

    private String Username;
    private String Password;

    //private final RestApi restApi;



    @Override
    public LoggedInUser authenticate(String username, String password, String serverAddress) {
        apiEndpoint = "/apps/kardia/api/crm/";
        String query = Host + apiEndpoint + collectionQueryOptions;

        this.Host = serverAddress + ":" + PORT;
        this.Base_Host_Name = serverAddress;
        this.Username = username;
        this.Password = password;

        try {
            // Attempt to pull information about the donor from the API

            String test = GET(query);
            // Unauthorized signals invalid ID
            // 404 not found signals incorrect username or password
            // Empty or null signals an incorrect server name
            if (test.equals("")) {
                errorType = ErrorType.ServerNotFound;
            } else if (test.contains("<H1>Unauthorized</H1>")) {
                errorType = ErrorType.Unauthorized;
            } else if (test.contains("404 Not Found")) {
                errorType = ErrorType.InvalidLogin;
            } else {
                loggedInUser = new LoggedInUser(username, password, serverAddress);
                loggedInUser.setPartnerId(getPartnerId(username));
            }
        }
        catch (Exception e) {
            // GET function throws an Exception if server not found
            //errorType = ErrorType.ServerNotFound;
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public String getPartnerId(String username) {
        apiEndpoint = "/apps/kardia/api/partner/Staff";
        String query = Host + apiEndpoint + collectionQueryOptions;
        String returnedPartnerId = "NaN";

        try {
            String queryResponse = GET(query);

            //Some amount of error handling on the response goes here.

            JSONObject jsonData = null;
            jsonData = new JSONObject(queryResponse);

            JSONArray jsonArray = jsonData.names();

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    // @id signals a new object but contains no information
                    if(!jsonArray.getString(i).equals("@id")) {
                        String kardiaLogin =
                                ((JSONObject) jsonData
                                        .get(jsonArray.get(i).toString()))
                                        .get("kardia_login").toString();
                        if (kardiaLogin.equals(username)) {
                            returnedPartnerId = ((JSONObject) jsonData
                                    .get(jsonArray.get(i).toString()))
                                    .get("partner_id").toString();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            errorType = ErrorType.ServerNotFound;
        }
        return returnedPartnerId;
    }

    @Override
    public List<UserIdentifier> getCollaboratees() {
        return null;
    }

    public String GET(String url) throws Exception {
        InputStream inputStream;
        String result = "";

        try {

            CredentialsProvider credProvider = new BasicCredentialsProvider();
            credProvider.setCredentials(new AuthScope(Base_Host_Name, 800),
                    new UsernamePasswordCredentials(Username, Password));

            DefaultHttpClient client = new DefaultHttpClient();

            client.setCredentialsProvider(credProvider);

            HttpResponse response = client.execute(new HttpGet(url));

            inputStream = response.getEntity().getContent();

            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "";
            }
        } catch (Exception e) {
            // Rethrow exception for validation server error
            throw new Exception();
        }
        return result;
    }

    /**
     * If there are results, change them into a string.
     *
     * @param in, the inputStream containing the results of the query (if any)
     * @return a string with the results of the query.
     * @throws IOException
     */
    private String convertInputStreamToString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line, result = "";

        while ((line = reader.readLine()) != null) {
            result += line;
        }
        in.close();
        return result;
    }

}
