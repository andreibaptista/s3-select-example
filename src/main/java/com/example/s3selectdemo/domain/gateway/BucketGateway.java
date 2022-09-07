package com.example.s3selectdemo.domain.gateway;

public interface BucketGateway {

    String returnItemFromBucket(String field, String value);

}
