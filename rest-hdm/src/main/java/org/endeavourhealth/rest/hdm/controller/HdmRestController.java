package org.endeavourhealth.rest.hdm.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${hdm-rest-api.context-path:/rest/hdm}/v1/crud", produces = {MediaType.APPLICATION_JSON_VALUE})
public class HdmRestController {
}
