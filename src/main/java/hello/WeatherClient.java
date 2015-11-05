
package hello;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import hello.wsdl.Forecast;
import hello.wsdl.ForecastReturn;
import hello.wsdl.GetCityForecastByZIP;
import hello.wsdl.GetCityForecastByZIPResponse;
import hello.wsdl.Temp;

public class WeatherClient {
	
	private static final Logger log = LoggerFactory.getLogger(WeatherClient.class);
	
	// check WDSL content by using: http://xmlgrid.net/
	private WebServiceTemplate caller;
	{
		//WebServiceTemplate class is thread-safe once configured
		caller = new WebServiceTemplate(); 
		// Jaxb2Marshaller creates JAXB context (one instance (per thread?)), and has 'marshalling' methods that create a marshaller on the fly (using the context) when doing the 'marshalling'
		//JAXB: context, marshalling: https://jaxb.java.net/guide/Performance_and_thread_safety.html
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("hello.wsdl");
		caller.setMarshaller(marshaller);
		caller.setUnmarshaller(marshaller);
	}

	public GetCityForecastByZIPResponse getCityForecastByZip(String zipCode) {
		GetCityForecastByZIP request = new GetCityForecastByZIP();
		request.setZIP(zipCode);

		log.info("Requesting forecast for " + zipCode);

		GetCityForecastByZIPResponse response = (GetCityForecastByZIPResponse) 
				caller.marshalSendAndReceive(
						"http://wsf.cdyne.com/WeatherWS/Weather.asmx",
						request,
						new SoapActionCallback("http://ws.cdyne.com/WeatherWS/GetCityForecastByZIP"));

		return response;
	}


	public void printResponse(GetCityForecastByZIPResponse response) {
		ForecastReturn forecastReturn = response.getGetCityForecastByZIPResult();

		if (forecastReturn.isSuccess()) {
			log.info("Forecast for " + forecastReturn.getCity() + ", " + forecastReturn.getState());

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			for (Forecast forecast : forecastReturn.getForecastResult().getForecast()) {

				Temp temperature = forecast.getTemperatures();

				log.info(String.format("%s %s %s°-%s°", format.format(forecast.getDate().toGregorianCalendar().getTime()),
						forecast.getDesciption(), temperature.getMorningLow(), temperature.getDaytimeHigh()));
				log.info("");
			}
		} else {
			log.info("No forecast received");
		}
	}

}
