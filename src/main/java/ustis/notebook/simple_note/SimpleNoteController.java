package ustis.notebook.simple_note;

import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/simple-note")
public class SimpleNoteController {
    private final SimpleNoteDao simpleNoteDao;

    public SimpleNoteController(SimpleNoteDao simpleNoteDao) {
        this.simpleNoteDao = simpleNoteDao;
    }

    @Deprecated
    @GetMapping("/")
    public ResponseEntity<List<SimpleNote>> getAll() {
        try {
            return ResponseEntity.ok(this.simpleNoteDao.getAll());
        } catch (DataAccessException exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimpleNote> getByPageId(@PathVariable Integer id) {
        Optional<SimpleNote> note;
        try {
            note = this.simpleNoteDao.getByPageId(id);
        } catch (DataAccessException exception) {
            return ResponseEntity.internalServerError().build();
        }
        return note.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<String> save(@RequestBody @Valid SimpleNote simpleNote) {
        try {
            this.simpleNoteDao.save(simpleNote);
            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody @Valid SimpleNote simpleNote ) {
        try {
            this.simpleNoteDao.update(simpleNote);
            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            this.simpleNoteDao.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }
}
