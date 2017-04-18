package com.ciandt.d1.rssbabel.controller;

import com.ciandt.d1.rssbabel.utils.CacheServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Class that provides an endpoint for health checking
 */
@RestController
@RequestMapping(value = "/api/rss-babel/v1/admin")
public class AdminController {

    static Logger logger = LoggerFactory.getLogger(AdminController.class.getName());

    private final CacheServices cacheServices;

    @Autowired
    public AdminController(CacheServices cacheServices) {
        this.cacheServices = cacheServices;
    }

    @RequestMapping(value = "/healthcheck", method = RequestMethod.GET)
    public ResponseEntity<HealthCheckResponse> ping() {

        logger.info("Request received for healthcheck... returning that everything is OK");

        HealthCheckResponse response = new HealthCheckResponse("Services are running OK",
                System.currentTimeMillis(), HttpServletResponse.SC_OK);

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/invalidateCache", method = RequestMethod.POST)
    public ResponseEntity invalidateCache() {

        logger.info("Invalidating cache...");
        cacheServices.invalidateAll();
        return ResponseEntity.ok().build();
    }

    public static class HealthCheckResponse {
        private String message;
        private Long timestamp;
        private Integer statusCode;

        public HealthCheckResponse(String message, Long timestamp, Integer statusCode) {
            this.message = message;
            this.timestamp = timestamp;
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }
    }
}
