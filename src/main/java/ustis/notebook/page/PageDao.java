package ustis.notebook.page;

import java.util.List;
import java.util.Optional;

public interface PageDao {
    int save(Page page);

    Optional<Page> getById(int id);

    void update(Page page);

    void deleteById(int id);

    List<Page> getAll();
}
