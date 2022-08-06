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
        res.setCode("200");
        res.setMessage("create a user");
        res.setData(user);
        res.setSuccess(true);
        return ResponseEntity.ok().body(res);
    }
}
