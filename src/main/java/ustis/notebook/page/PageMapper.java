package ustis.notebook.page;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PageMapper implements RowMapper<Page> {
    @Override
    public Page mapRow(ResultSet rs, int i) throws SQLException {
        Page page = new Page();
        page.setId(rs.getInt("id"));
        page.setName(rs.getString("name"));
        page.setNoteType(rs.getString("note_type"));
        return page;
    }
}