package ustis.notebook.page;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class PageDaoImpl implements PageDao {
    private final JdbcTemplate jdbcTemplate;

    public PageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(Page page) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(
                    con -> {
                        PreparedStatement ps =
                                con.prepareStatement("INSERT INTO page ( name, note_type) VALUES ( 1, ?, ? );",
                                        Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, page.getName());
                        ps.setString(2, page.getNoteType());
                        return ps;
                    }, keyHolder
            );
            return (Integer) keyHolder.getKeys().get("id");
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public Optional<Page> getById(int id) throws EmptyResultDataAccessException {
        try {
            return Optional.ofNullable(this.jdbcTemplate.queryForObject(
                    "SELECT id, name, note_type FROM page WHERE id = ?",
                    new Object[]{id},
                    new PageMapper()));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public void update(Page page) {
        try {
            this.jdbcTemplate.update("UPDATE page SET name = ? WHERE id = ?",
                    new Object[]{page.getName(), page.getId().toString()}
            );
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public void deleteById(int id) {
        try {
            this.jdbcTemplate.update(
                    "DELETE FROM page where id=?",
                    new Object[]{id}
            );
        } catch (EmptyResultDataAccessException exception) {
            throw new RuntimeException("Page not found");
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public List<Page> getAll() {
        try {
            return this.jdbcTemplate.query("SELECT id, name, note_type FROM page", new PageMapper());
        } catch (EmptyResultDataAccessException exception) {
            return Collections.emptyList();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }
}
