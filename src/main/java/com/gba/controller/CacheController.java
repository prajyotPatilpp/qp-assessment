package com.gba.controller;

import com.gba.beans.ResponseBean;
import com.gba.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/cache")
public class CacheController {
    private static final Logger logger = LoggerFactory.getLogger("CacheController");
    @Autowired
    AdminService adminService;

    // API to clear the cache data once every day
    @GetMapping("/refreshCache")
    @Scheduled(cron = "${Scheduler.refreshCache}")
    public ResponseEntity<ResponseBean> refreshCache(){
        logger.info("Entered method refreshCache ");
        try {
            ResponseBean responseMessage = adminService.clearCache();
            return ResponseEntity.ok(responseMessage);
        } catch (Exception e) {
            logger.error("Error occurred while processing refreshCache" + e);
            ResponseBean errorResponse = new ResponseBean();
            errorResponse.setStatus("Failed");
            errorResponse.setStatusCode(500);
            errorResponse.setStatusMsg("Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
