package com.kduytran.elasticsearch.search.util;

import com.kduytran.elasticsearch.search.SearchRequestDTO;
import lombok.experimental.UtilityClass;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@UtilityClass
public class SearchUtils {

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final SearchRequestDTO dto) {
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .postFilter(getQueryBuilder(dto));
        SearchRequest request = new SearchRequest(indexName);

        if (dto.getSortBy() != null) {
            builder = builder.sort(
                    dto.getSortBy(),
                    dto.getSortOrder() != null ? dto.getSortOrder() : SortOrder.ASC
            );
        }

        final int page = dto.getPage();
        final int size = dto.getSize();
        final int from = page <= 0 ? 0 : page * size;
        builder.from(from).size(size);

        request.source(builder);
        return request;
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final String field,
                                                   final Date date) {
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .postFilter(getQueryBuilder(field, date));
        SearchRequest request = new SearchRequest(indexName);
        request.source(builder);
        return request;
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final SearchRequestDTO dto,
                                                   final Date date) {
        QueryBuilder searchQuery = getQueryBuilder(dto);
        QueryBuilder dateQuery = getQueryBuilder("created", date);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(searchQuery)
                .must(dateQuery);
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .postFilter(boolQueryBuilder);
        SearchRequest request = new SearchRequest(indexName);

        if (dto.getSortBy() != null) {
            builder = builder.sort(
                    dto.getSortBy(),
                    dto.getSortOrder() != null ? dto.getSortOrder() : SortOrder.ASC
            );
        }

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

    public static QueryBuilder getQueryBuilder(final String field, final Date date) {
        return QueryBuilders.rangeQuery(field).gte(date);
    }

}
