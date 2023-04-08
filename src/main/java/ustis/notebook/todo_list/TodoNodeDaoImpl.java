package ustis.notebook.todo_list;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoNodeDaoImpl implements TodoNodeDao {
    private final JdbcTemplate jdbcTemplate;

    public TodoNodeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(TodoNode todoNode) throws RuntimeException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            if (todoNode.getParentId() != null)
                this.jdbcTemplate.update(
                        con -> {
                            PreparedStatement ps = con.prepareStatement("INSERT INTO  todo_node (parent_node_id,text, status) VALUES (?,?,?)",
                                    Statement.RETURN_GENERATED_KEYS);
                            ps.setInt(1, todoNode.getParentId());
                            ps.setString(2, todoNode.getText());
                            ps.setBoolean(3, todoNode.getStatus());
                            return ps;
                        }, keyHolder
                );
            else if (todoNode.getPageId() != null)
                this.jdbcTemplate.update(
                        con -> {
                            PreparedStatement ps = con.prepareStatement("INSERT INTO todo_node(page_id, text, status) VALUES (?,?,?)",
                                    Statement.RETURN_GENERATED_KEYS);
                            ps.setInt(1, todoNode.getPageId());
                            ps.setString(2, todoNode.getText());
                            ps.setBoolean(3, todoNode.getStatus());
                            return ps;
                        }, keyHolder
                );
            return (Integer) keyHolder.getKeys().get("id");
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public Optional<TodoNode> getById(int id) {
        try {
            return Optional.ofNullable(this.jdbcTemplate.queryForObject(
                    "SELECT id, page_id, parent_node_id, text, status FROM todo_node WHERE id=?",
                    new Object[]{id},
                    new TodoNodeMapper()
            ));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public void update(TodoNode todoNode) {
        try {
            if (Optional.ofNullable(todoNode.getPageId()).orElse(0) != 0)
                this.jdbcTemplate.update(
                        "UPDATE todo_node SET page_id =?, text = ?, status = ? WHERE id = ?",
                        new Object[]{todoNode.getPageId(), todoNode.getText(), todoNode.getStatus(), todoNode.getId()}
                );
            if (Optional.ofNullable(todoNode.getParentId()).orElse(0) != 0)
                this.jdbcTemplate.update(
                        "UPDATE todo_node SET parent_node_id =?, text = ?, status = ? WHERE id = ?",
                        new Object[]{todoNode.getParentId(), todoNode.getText(), todoNode.getStatus(), todoNode.getId()}
                );
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public void deleteById(int id) {
        try {
            this.jdbcTemplate.update(
                    "DELETE FROM todo_node WHERE id=?",
                    new Object[]{id}
            );
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public void deleteByProperties(TodoNode node) {
        try {
            if (node.getPageId() != 0)
                this.jdbcTemplate.update(
                        "DELETE FROM todo_node WHERE page_id = ? AND text = ? AND status = ?",
                        new Object[]{node.getPageId(), node.getText(), node.getStatus()}
                );
            if (node.getParentId() != 0)
                this.jdbcTemplate.update(
                        "DELETE FROM todo_node WHERE parent_node_id = ? AND text = ? AND status = ?",
                        new Object[]{node.getParentId(), node.getText(), node.getStatus()}
                );
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public List<TodoNode> getAll() {
        try {
            return this.jdbcTemplate.query(
                    "SELECT id, page_id, parent_node_id, text, status FROM todo_node",
                    new TodoNodeMapper()
            );
        } catch (EmptyResultDataAccessException exception) {
            return Collections.emptyList();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public List<TodoNode> getTodoNodeTreeByPageId(Integer id) {
        try {
            return this.jdbcTemplate.query(
                    "WITH RECURSIVE todo_node_tree AS ( SELECT id, page_id, parent_node_id, text, status FROM todo_node \n" +
                            "WHERE page_id = ?\n" +
                            "UNION SELECT t.id, t.page_id, t.parent_node_id, t.text, t.status FROM todo_node t \n" +
                            "INNER JOIN todo_node_tree tt ON t.parent_node_id= tt.id) SELECT * FROM todo_node_tree;",
                    new Object[]{id},
                    new TodoNodeMapper());
        } catch (EmptyResultDataAccessException exception) {
            return Collections.emptyList();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }

    @Override
    public List<TodoNode> getTodoNodeTreesByPageIds(List<Integer> ids) {
        try {
            String query = "WITH RECURSIVE todo_node_tree AS ( SELECT id, parent_node_id, text, status FROM todo_node \n" +
                    "WHERE page_id = ?\n" +
                    "UNION SELECT t.id, t.parent_node_id, t.text, t.status FROM todo_node t \n" +
                    "INNER JOIN todo_node_tree tt ON t.parent_node_id= tt.id) SELECT * FROM todo_node_tree;";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("pageIds", ids);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            return namedParameterJdbcTemplate.query(query, parameters, new TodoNodeMapper());
        } catch (EmptyResultDataAccessException exception) {
            return Collections.emptyList();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Database error occurred");
        }
    }
}
