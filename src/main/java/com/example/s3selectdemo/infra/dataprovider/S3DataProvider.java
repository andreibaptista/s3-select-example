package com.example.s3selectdemo.infra.dataprovider;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ExpressionType;
import com.amazonaws.services.s3.model.InputSerialization;
import com.amazonaws.services.s3.model.JSONInput;
import com.amazonaws.services.s3.model.JSONOutput;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.OutputSerialization;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.SelectObjectContentEvent;
import com.amazonaws.services.s3.model.SelectObjectContentEventVisitor;
import com.amazonaws.services.s3.model.SelectObjectContentRequest;
import com.amazonaws.services.s3.model.SelectObjectContentResult;
import com.example.s3selectdemo.domain.gateway.BucketGateway;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class S3DataProvider implements BucketGateway {

    private static final String BUCKET_NAME = "nobel-prize";
    private final AmazonS3 s3Client;

    public S3DataProvider(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String returnItemFromBucket(String field, String value) {

        ObjectListing objectListing = s3Client.listObjects(BUCKET_NAME);

        for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {

            SelectObjectContentRequest request = new SelectObjectContentRequest();
            request.setBucketName(BUCKET_NAME);
            request.setKey(summary.getKey());
            request.setExpression("SELECT * FROM S3Object[*].prizes[*].laureates[*] s  where s." + field +" = '"+ value + "'");
            request.setExpressionType(ExpressionType.SQL);

            InputSerialization inputSerialization = new InputSerialization();
            inputSerialization.setJson(new JSONInput().withType("DOCUMENT"));
            OutputSerialization outputSerialization = new OutputSerialization();
            outputSerialization.setJson(new JSONOutput());

            request.setInputSerialization(inputSerialization);
            request.setOutputSerialization(outputSerialization);

            try (SelectObjectContentResult result = s3Client.selectObjectContent(request)) {

                // Perform some per-stream output of details of current query
                try (InputStream resultInputStream = result.getPayload().getRecordsInputStream(
                        new SelectObjectContentEventVisitor() {
                            @Override
                            public void visit(SelectObjectContentEvent.StatsEvent event) {
                                System.out.println("Total events received: " + event.getDetails().getBytesReturned().intValue());
                            }

                            @Override
                            public void visit(SelectObjectContentEvent.EndEvent event) {
                                System.out.println("Result is complete");
                            }
                        }
                )) {

                    return new String(com.amazonaws.util.IOUtils.toByteArray(resultInputStream)).trim();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}

