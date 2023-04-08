package ustis.notebook.simple_note;

import java.util.List;
import java.util.Optional;

public interface SimpleNoteDao {
    void save(SimpleNote simpleNote);

    Optional<SimpleNote> getById(Integer id);

    Optional<SimpleNote> getByPageId(Integer id);

    void update(SimpleNote simpleNote);

    void deleteById(Integer id);

    List<SimpleNote> getAll();

    List<SimpleNote> getAllByPageIds(List<Integer> ids);
}
