package ro.pub.cs.systems.eim.practicaltest02.model;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;

public class WeatherForecastInformation {

    private String hour;
    private String minute;
    private String cmd;
    
    private String cmdall;

    public WeatherForecastInformation() {
        this.hour = null;
        this.minute = null;
       
    }

    public WeatherForecastInformation(
            String temperature,
            String windSpeed,
            String cmd, String port
  ) {
        this.hour = temperature;
        this.minute = windSpeed;
        this.cmd = cmd;
        this.cmdall = cmdall;
    }
    
    public WeatherForecastInformation(
           String cmdall
  ) {
    	  this.cmdall = cmdall;}

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getHour() {
        return hour;
    }
    
    
    public void setMinute(String minute) {
        this.minute= minute;
    }

    public String getMinute() {
        return minute;
    }
    
    public void setCmd(String minute) {
        this.cmd= minute;
    }

    public String getCmd() {
        return cmd;
    }
    


//    @Override
//    public String toString() {
//        return Constants.TEMPERATURE + ": " + temperature + "\n\r" +
//                Constants.WIND_SPEED + ": " + windSpeed + "\n\r" +
//                Constants.CONDITION + ": " + condition + "\n\r" +
//                Constants.PRESSURE + ": " + pressure + "\n\r" +
//                Constants.HUMIDITY + ": " + humidity;
//    }

}
