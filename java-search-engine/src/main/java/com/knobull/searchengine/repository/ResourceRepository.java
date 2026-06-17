package com.knobull.searchengine.repository;

import com.knobull.searchengine.model.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResourceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ResourceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Resource> searchByEmbedding(float[] embedding) {
        StringBuilder vector = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                vector.append(',');
            }
            vector.append(embedding[i]);
        }
        vector.append(']');

        return jdbcTemplate.query(
                """
                SELECT id, title, description, link
                FROM resources
                ORDER BY embedding <=> ?::vector
                LIMIT 5
                """,
                (rs, rowNum) -> {
                    Resource resource = new Resource();
                    resource.id = rs.getString("id");
                    resource.title = rs.getString("title");
                    resource.description = rs.getString("description");
                    resource.link = rs.getString("link");
                    return resource;
                },
                vector.toString()
        );
    }
}
