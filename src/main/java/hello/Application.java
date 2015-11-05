
package hello;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import hello.wsdl.GetCityForecastByZIPResponse;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	/**
	 * CommandlineRunner are run by a spring boot application when found in the 'configuration'.
	 */
	@Bean
	CommandLineRunner lookup() {
		return args -> {
			String zipCode = "94304";

			if (args.length > 0) {
				zipCode = args[0];
			}
			WeatherClient weatherClient = new WeatherClient();
			GetCityForecastByZIPResponse response = weatherClient.getCityForecastByZip(zipCode);
			weatherClient.printResponse(response);
		};
	}

}
