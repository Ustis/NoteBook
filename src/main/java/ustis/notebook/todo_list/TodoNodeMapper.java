package ustis.notebook.todo_list;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TodoNodeMapper implements RowMapper<TodoNode> {
    @Override
    public TodoNode mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TodoNode(
                rs.getInt("id"),
                rs.getString("text"),
                rs.getBoolean("status"),
                rs.getInt("page_id"),
                rs.getInt("parent_node_id"));
    }
}
