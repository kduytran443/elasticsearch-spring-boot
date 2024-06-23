package com.kduytran.elasticsearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kduytran.elasticsearch.document.VehicleDocument;
import com.kduytran.elasticsearch.helper.Indices;
import com.kduytran.elasticsearch.search.SearchRequestDTO;
import com.kduytran.elasticsearch.search.util.SearchUtils;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    public boolean index(final VehicleDocument[] documents) {
        boolean result = true;
        for (VehicleDocument document : documents) {
            result = index(document) == false ? false : result;
        }
        return result;
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

    public List<VehicleDocument> getAllCreatedSince(final Date date) {
        final SearchRequest request = SearchUtils.buildSearchRequest(Indices.VEHICLE_INDEX, "created", date);

        return invokeSearchRequest(request);
    }

    public List<VehicleDocument> search(final SearchRequestDTO dto) {
        final SearchRequest request = SearchUtils.buildSearchRequest(Indices.VEHICLE_INDEX, dto);

        return invokeSearchRequest(request);
    }

    private List<VehicleDocument> invokeSearchRequest(SearchRequest request) {
        if (request == null) {
            LOGGER.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<VehicleDocument> list = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                list.add(
                        mapper.readValue(hit.getSourceAsString(), VehicleDocument.class)
                );
            }
            return list;
        } catch (IOException e) {
            LOGGER.info("Failed to make search response");
            return Collections.emptyList();
        }
    }

}
