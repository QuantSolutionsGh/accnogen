package com.databank.accnogen.controller;



import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class MainController {


    @Value("${accnogen.restendpoint}")
    private String restEndPoint;


    @PostMapping(value="/upload")
    public void uploadFile(@RequestParam("file")MultipartFile file,@RequestParam("email")String email,HttpServletResponse httpServletResponse) throws IOException {
        byte[]_file =null;
        RestTemplate restTemplate = new RestTemplate();
        if (!file.getOriginalFilename().isEmpty()){
            try {
                ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                };
                LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
                map.add("file1", fileAsResource);
                map.add("email",email);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);


                ResponseEntity<byte[]> response = restTemplate.exchange(restEndPoint,
                        HttpMethod.POST, entity,byte[].class);


                if (response.getStatusCode().equals(HttpStatus.OK)) {
                   // success = true;
                    httpServletResponse.setContentType("application/vnd.ms-excel");

                    httpServletResponse.addHeader("Content-Disposition", "attachment; filename=\"autogen_accnos.xlsx\"");

                    IOUtils.write(response.getBody(),httpServletResponse.getOutputStream());



                    httpServletResponse.getOutputStream().flush();
                    httpServletResponse.getOutputStream().close();
                }
            } catch (Exception e) {

            }
        }

    }





    @RequestMapping(value={"/","/index"},method= RequestMethod.GET)
    public String showMainPage(Model model){



        return "index";

    }
}
