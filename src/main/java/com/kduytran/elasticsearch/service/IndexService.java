package com.kduytran.elasticsearch.service;

import com.kduytran.elasticsearch.helper.Utils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexService.class);
    public static final List<String> INDICES_TO_CREATE = List.of("vehicle");
    public static final String SETTINGS_PATH = "static/es-settings.json";
    public static final String MAPPING_PATH_FORMAT = "static/mappings/%s.json";
    public static final boolean deleteExisting = false;

    private final RestHighLevelClient client;

    @PostConstruct
    public void handle() {
        recreateIndices(deleteExisting);
    }

    public void recreateIndices(final boolean deleteExisting) {
        final String settings = Utils.loadAsString(SETTINGS_PATH);
        if (settings == null) {
            LOGGER.error("Failed to create index because of null settings");
        }
        for (final String indexName : INDICES_TO_CREATE) {
            try {
                boolean indexExists = client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
                if (indexExists) {
                    if (!deleteExisting) {
                        LOGGER.error("Index '{}' existed, skipped", indexName);
                        continue;
                    }
                    LOGGER.error("Index '{}' existed, deleted", indexName);
                    client.indices().delete(
                            new DeleteIndexRequest(indexName),
                            RequestOptions.DEFAULT
                    );
                }
                final String mappings = Utils.loadAsString(String.format(MAPPING_PATH_FORMAT, indexName));
                if (mappings == null) {
                    LOGGER.error("Failed to create index with name '{}'", indexName);
                }
                final CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
                createIndexRequest.settings(settings, XContentType.JSON);
                createIndexRequest.mapping(mappings, XContentType.JSON);

                client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

}
