package com.self.notificationService.controller;

import com.self.notificationService.constants.ControllerConstants;
import com.self.notificationService.model.dto.response.GenericResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerConstants.NOTIFICATIONS)
public class HealthCheck {

    @GetMapping(ControllerConstants.HEALTH_CHECK)
    public GenericResponse<String> healthCheck() {
        return GenericResponse.buildSuccess("HEALTH CHECKUP IS SUCCESSFUL");
    }

}
