package uk.ac.ebi.pride.archive.repo.ws.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class IndexController {

    @RequestMapping(method = RequestMethod.GET, path = {"/"})
    public String getSwaggerUI(HttpServletRequest request){
        String url = request.getRequestURL().toString();

        log.debug("redirect for "+url+" => swagger-ui");
        //for "/" URL, we redirect client to actual swagger page
        return "redirect:swagger-ui";
    }

}
