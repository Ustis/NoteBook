package ustis.notebook.simple_note;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class SimpleNoteDaoImpl implements SimpleNoteDao {
    private final JdbcTemplate jdbcTemplate;

    public SimpleNoteDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(SimpleNote simpleNote) {
        try {
            this.jdbcTemplate.update(
                    "INSERT INTO simple_note ( page_id, text) VALUES (?, ?)",
                    new Object[]{simpleNote.getPageId(), simpleNote.getText()}
            );
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public Optional<SimpleNote> getById(Integer id) {
        try {
            return Optional.ofNullable(this.jdbcTemplate.queryForObject(
                    "SELECT id, text, page_id FROM simple_note WHERE id = ?",
                    new Object[]{id},
                    new SimpleNoteMapper()));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public Optional<SimpleNote> getByPageId(Integer id) {
        try {
            return Optional.ofNullable(this.jdbcTemplate.queryForObject(
                    "SELECT id, text, page_id FROM simple_note WHERE page_id = ?",
                    new Object[]{id},
                    new SimpleNoteMapper()));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public void update(SimpleNote simpleNote) {
        try {
            this.jdbcTemplate.update(
                    "UPDATE simple_note SET text = ? WHERE id = ?",
                    new Object[]{simpleNote.getText(), simpleNote.getId()}
            );
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public void deleteById(Integer id) {
        try {
            this.jdbcTemplate.update(
                    "DELETE FROM simple_note where id=?",
                    new Object[]{id});
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public List<SimpleNote> getAll() {
        try {
            return this.jdbcTemplate.query(
                    "SELECT id, text, page_id FROM simple_note",
                    new SimpleNoteMapper());
        } catch (EmptyResultDataAccessException exception) {
            return Collections.emptyList();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public List<SimpleNote> getAllByPageIds(List<Integer> ids) {
        try {
            String query = "SELECT * FROM simple_note WHERE page_id IN (:pageIds)";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("pageIds", ids);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            return namedParameterJdbcTemplate.query(query, parameters, new SimpleNoteMapper());
        } catch (EmptyResultDataAccessException exception) {
            return Collections.emptyList();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }
}
