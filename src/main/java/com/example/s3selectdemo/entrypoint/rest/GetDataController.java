package com.example.s3selectdemo.entrypoint.rest;

import com.example.s3selectdemo.usecase.GetItemUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class GetDataController {

    private final GetItemUseCase getItemsUseCase;


    public GetDataController(GetItemUseCase getItemsUseCase) {
        this.getItemsUseCase = getItemsUseCase;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> getItemsTradeReturn(@RequestParam String field, String value) {
        return ResponseEntity.ok(getItemsUseCase.getItems(field, value));
    }




}

