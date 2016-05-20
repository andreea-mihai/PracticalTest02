package ro.pub.cs.systems.eim.practicaltest02.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.WeatherForecastInformation;
import android.util.Log;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader != null && printWriter != null) {
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)!");
                    String alarm = bufferedReader.readLine();
                    HashMap<String, WeatherForecastInformation> data = serverThread.getData();
                    WeatherForecastInformation weatherForecastInformation = null;
                    if (alarm != null && !alarm.isEmpty()) {
             
                        if (data.containsKey(alarm)) {
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                            weatherForecastInformation = data.get(alarm);
                        } else {
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                            HttpClient httpClient = new DefaultHttpClient();
                            
      
                           
                            
                            HttpGet httpPost = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            
                            String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                            if (pageSourceCode != null) {
                                Document document = Jsoup.parse(pageSourceCode);
                                Element element = document.child(0);
                                Elements scripts = element.getElementsByTag(Constants.SCRIPT_TAG);
                                for (Element script : scripts) {

                                    String scriptData = script.data();

                                    if (scriptData.contains(Constants.SEARCH_KEY)) {
                                        int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
                                        scriptData = scriptData.substring(position);

                                        JSONObject content = new JSONObject(scriptData);

                                        JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);
                                        String temperature = currentObservation.getString(Constants.TEMPERATURE);
                                        String windSpeed = currentObservation.getString(Constants.WIND_SPEED);
                                        String condition = currentObservation.getString(Constants.CONDITION);
                                        

//                                        weatherForecastInformation = new WeatherForecastInformation(
//                                                hour,
//                                                minute,cmd
//                          );
//
//                                        serverThread.setData(city, weatherForecastInformation);
//                                        break;
                                    }
                                }
                            } else {
                                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                            }
                        }

//                        if (weatherForecastInformation != null) {
//                            String result = null;
//                            if (Constants.ALL.equals(informationType)) {
//                                result = weatherForecastInformation.toString();
//                            } else if (Constants.TEMPERATURE.equals(informationType)) {
//                                result = weatherForecastInformation.getTemperature();
//                            } else if (Constants.WIND_SPEED.equals(informationType)) {
//                                result = weatherForecastInformation.getWindSpeed();
//                            } else if (Constants.CONDITION.equals(informationType)) {
//                                result = weatherForecastInformation.getCondition();
//                            } else if (Constants.HUMIDITY.equals(informationType)) {
//                                result = weatherForecastInformation.getHumidity();
//                            } else if (Constants.PRESSURE.equals(informationType)) {
//                                result = weatherForecastInformation.getPressure();
//                            } else {
//                                result = "Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
//                            }
//                            printWriter.println(result);
//                            printWriter.flush();
//                        } else {
//                            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast information is null!");
//                        }

                    } else {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!");
                    }
                } else {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
                }
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            } catch (JSONException jsonException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
                if (Constants.DEBUG) {
                    jsonException.printStackTrace();
                }
            }
        } else {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
        }
    }

}
