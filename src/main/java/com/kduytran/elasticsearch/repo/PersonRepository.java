package com.kduytran.elasticsearch.repo;

import com.kduytran.elasticsearch.document.PersonDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PersonRepository extends ElasticsearchRepository<PersonDocument, String> {
}
