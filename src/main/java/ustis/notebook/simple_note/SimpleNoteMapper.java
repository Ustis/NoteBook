package ustis.notebook.simple_note;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SimpleNoteMapper implements RowMapper<SimpleNote> {
    @Override
    public SimpleNote mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SimpleNote(
                rs.getInt("id"),
                rs.getString("text"),
                rs.getInt("page_id")
        );
    }
}
