package com.example.s3selectdemo.usecase.impl;

import com.example.s3selectdemo.domain.gateway.BucketGateway;
import com.example.s3selectdemo.usecase.GetItemUseCase;
import org.springframework.stereotype.Service;

@Service
public class GetItemUseCaseImpl implements GetItemUseCase {

    private final BucketGateway bucketGateway;

    public GetItemUseCaseImpl(BucketGateway bucketGateway) {
        this.bucketGateway = bucketGateway;
    }


    @Override
    public String getItems(String field, String value) {
        return bucketGateway.returnItemFromBucket(field, value);
    }
}
