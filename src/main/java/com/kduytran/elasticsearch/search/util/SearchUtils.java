package com.kduytran.elasticsearch.search.util;

import com.kduytran.elasticsearch.search.SearchRequestDTO;
import lombok.experimental.UtilityClass;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.CollectionUtils;

import java.util.List;

@UtilityClass
public class SearchUtils {

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final SearchRequestDTO dto) {
        final SearchSourceBuilder builder = new SearchSourceBuilder()
                .postFilter(getQueryBuilder(dto));
        SearchRequest request = new SearchRequest(indexName);
        request.source(builder);
        return request;
    }

    public static QueryBuilder getQueryBuilder(final SearchRequestDTO dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getFields())) {
            return null;
        }

        List<String> fields = dto.getFields();
        if (fields.size() > 1) {
            MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(dto.getSearchTerm())
                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                    .operator(Operator.AND);
            fields.forEach(queryBuilder::field);
            return queryBuilder;
        }

        return fields.stream()
                .findFirst()
                .map(field -> QueryBuilders.matchQuery(field, dto.getSearchTerm())
                        .operator(Operator.AND))
                .orElse(null);
    }

}
