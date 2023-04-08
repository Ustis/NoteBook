package ustis.notebook.todo_list;

import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todo-node")
public class TodoNodeController {
    private final TodoNodeDao todoNodeDao;

    public TodoNodeController(TodoNodeDao todoNodeDao) {
        this.todoNodeDao = todoNodeDao;
    }

    @GetMapping("/")
    public ResponseEntity<List<TodoNode>> getAll() {
        try {
            return ResponseEntity.ok(this.todoNodeDao.getAll());
        } catch (DataAccessException exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<TodoNode>> getById(@PathVariable Integer id) {
        List<TodoNode> nodes;
        try {
            nodes = this.todoNodeDao.getTodoNodeTreeByPageId(id);
        } catch (DataAccessException exception) {
            return ResponseEntity.internalServerError().build();
        }
        if (!nodes.isEmpty())
            return ResponseEntity.ok(nodes);
        else
            return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<String> save(@RequestBody @Valid TodoNode node) {
        try {
            Integer id = this.todoNodeDao.save(node);
            return ResponseEntity.ok(id.toString());
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }

    // ВАЖНО если необходимо будет реализовать перемещение ноды по дереву потребуется переписать,
    // т.к. потребуется престройка дерева
    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody @Valid TodoNode node) {
        try {
            this.todoNodeDao.update(node);
            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            this.todoNodeDao.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }
}
