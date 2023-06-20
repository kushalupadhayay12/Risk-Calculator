package com.example.dynamic;

import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskSchedulerService {

	Logger log = LoggerFactory.getLogger(TaskSchedulerService.class);
	
	@Scheduled(fixedRate = 500000)
	public void run() {
		log.info("Runnning......................");
		String url = "http://localhost:9090/calculate";
		
		
		try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            log.info("GET request to my-endpoint, response code: " + responseCode);

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
