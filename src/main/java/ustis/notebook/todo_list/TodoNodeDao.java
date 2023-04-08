package ustis.notebook.todo_list;

import java.util.List;
import java.util.Optional;

public interface TodoNodeDao {
    int save(TodoNode todoNode);

    Optional<TodoNode> getById(int id);

    void update(TodoNode todoNode);

    void deleteById(int id);

    void deleteByProperties(TodoNode node);

    List<TodoNode> getAll();

    List<TodoNode> getTodoNodeTreeByPageId(Integer id);

    List<TodoNode> getTodoNodeTreesByPageIds(List<Integer> ids);
}
