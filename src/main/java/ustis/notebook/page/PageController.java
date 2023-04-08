package ustis.notebook.page;

import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ustis.notebook.simple_note.SimpleNote;
import ustis.notebook.simple_note.SimpleNoteDao;
import ustis.notebook.todo_list.TodoNode;
import ustis.notebook.todo_list.TodoNodeDao;

import java.util.List;

@RestController
@RequestMapping("/page")
public class PageController {
    private final PageDao pageDao;

    private final SimpleNoteDao simpleNoteDao;

    private final TodoNodeDao todoNodeDao;

    public PageController(PageDao pageDao, SimpleNoteDao simpleNoteDao, TodoNodeDao todoNodeDao) {
        this.pageDao = pageDao;
        this.simpleNoteDao = simpleNoteDao;
        this.todoNodeDao = todoNodeDao;
    }

    @GetMapping("/")
    public ResponseEntity<List<Page>> getAll() {
        try {
            return ResponseEntity.ok(this.pageDao.getAll());
        } catch (DataAccessException exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> save(@RequestBody @Valid Page page) {
        try {
            Integer savedPageId = this.pageDao.save(page);
            if(page.getNoteType().equals("Simple note"))
                simpleNoteDao.save(new SimpleNote(savedPageId));
            if (page.getNoteType().equals("Todo note"))
                todoNodeDao.save(new TodoNode(savedPageId));

            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }
    
    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody @Valid Page page) {
        try {
            this.pageDao.update(page);
            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            this.pageDao.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }
}
