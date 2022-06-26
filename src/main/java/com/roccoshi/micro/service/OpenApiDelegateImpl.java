package com.roccoshi.micro.service;

import com.roccoshi.micro.web.api.ApiUtil;
import com.roccoshi.micro.web.api.UserApiDelegate;
import com.roccoshi.micro.web.api.model.Response;
import com.roccoshi.micro.web.api.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OpenApiDelegateImpl implements UserApiDelegate {

    @Override
    public ResponseEntity<Response> saveUser(User user) {
        Response res = new Response();
        getRequest().ifPresent(request -> {
            res.setMessage("catched" + request.toString());
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    System.out.println("the request is application/json format");
                    break;
                }
            }
        });
        res.setCode("200");
        return ResponseEntity.ok().body(res);
    }
}
