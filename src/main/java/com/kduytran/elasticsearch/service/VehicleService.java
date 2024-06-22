package com.kduytran.elasticsearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kduytran.elasticsearch.document.VehicleDocument;
import com.kduytran.elasticsearch.helper.Indices;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class VehicleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleService.class);
    private final RestHighLevelClient client;
    private final ObjectMapper mapper;

    public boolean index(final VehicleDocument document) {
        try {
            final String documentString = mapper.writeValueAsString(document);
            IndexRequest request = new IndexRequest(Indices.VEHICLE_INDEX);
            request.id(document.getId());
            request.source(documentString, XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().equals(RestStatus.OK);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    public VehicleDocument getById(final String id) {
        try {
            final GetResponse response = client.get(
                    new GetRequest(Indices.VEHICLE_INDEX, id),
                    RequestOptions.DEFAULT
            );
            if (response == null || response.isSourceEmpty()) {
                return null;
            }

            return mapper.readValue(response.getSourceAsString(), VehicleDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
