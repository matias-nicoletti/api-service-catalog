package com.matiasnicoletti.apiservicecatalog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class HomeController {

  @RequestMapping(value = "/service-catalog", method = RequestMethod.GET)
  public String index() {
    return "index.html";
  }

}
