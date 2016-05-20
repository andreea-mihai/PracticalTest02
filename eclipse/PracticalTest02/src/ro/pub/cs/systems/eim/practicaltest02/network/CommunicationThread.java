package ro.pub.cs.systems.eim.practicaltest02.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

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
             
                        if (data.containsKey(socket.getInetAddress())) {
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                            weatherForecastInformation = data.get(alarm);
                        } else {
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                            HttpClient httpClient = new DefaultHttpClient();
                            
                            
                            
                            HttpGet httpPost = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            
                            String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                            
                            if (pageSourceCode != null) {
                            	
                            	JSONObject content = new JSONObject(pageSourceCode);
                                
                                String time = content.getString("time");
                                
                                Date utcDate = new Date(time);
                                
                            	int hour = utcDate.getHours();
                            	int minute = utcDate.getMinutes();
                            	
                    System.out.println(hour);
                    Log.e("cheeeck", hour + " ");
//                                        
                                        String[] al = alarm.split(",");
                                        
                                        if (Integer.parseInt(al[1]) >= hour && Integer.parseInt(al[2]) >= minute)
                                        weatherForecastInformation = new WeatherForecastInformation(
                                               alarm );
//
                                        serverThread.setData(socket.getInetAddress().toString(), weatherForecastInformation);
                                      
                                    }
//                                }

                        }


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
