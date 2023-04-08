CREATE TABLE page
(
    id   SERIAL PRIMARY KEY,
    name CHARACTER VARYING(128) DEFAULT ''
    note_type CHARACTER VARYING (16)
);

CREATE UNIQUE INDEX page_PK on page (id);

CREATE TABLE simple_note
(
    id      SERIAL PRIMARY KEY,
    page_id INTEGER REFERENCES page (id) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    text    TEXT DEFAULT ''
);

CREATE UNIQUE INDEX simple_note_PK on simple_note (id);

CREATE TABLE todo_node
(
    id             SERIAL PRIMARY KEY,
    page_id        INTEGER REFERENCES page (id) ON UPDATE CASCADE ON DELETE CASCADE,
    parent_todo_id INTEGER REFERENCES todo_node (id) ON UPDATE CASCADE ON DELETE SET NULL,
    text           TEXT    DEFAULT '',
    status         Boolean DEFAULT FALSE
);



CREATE OR REPLACE FUNCTION public.rebuild_todo_tree()
    RETURNS trigger
    LANGUAGE plpgsql
AS
$function$
DECLARE
    childs       integer[];
    child_count  INTEGER;
    child        INTEGER;
    root_page_id INTEGER;
BEGIN
    child_count := (SELECT COUNT(*) FROM todo_node WHERE parent_node_id = OLD.id);

    IF child_count = 0 THEN
        RETURN OLD;
    END IF;

    childs := ARRAY(SELECT id FROM todo_node where parent_node_id = OLD.id);

    root_page_id := (SELECT page_id from todo_node WHERE id = OLD.id);
    IF root_page_id IS NOT NULL THEN
        FOREACH child in ARRAY childs
            Loop
                UPDATE todo_node SET page_id = root_page_id where id = child;
            END LOOP;
        RETURN OLD;
    END IF;

    IF child_count != 0 THEN
        FOREACH child in ARRAY childs
            LOOP
                UPDATE todo_node SET parent_node_id = OLD.parent_node_id Where id = child;
            END LOOP;
        RETURN OLD;
    END IF;

    RETURN OLD;
END;
$function$;

CREATE TRIGGER rebuild_todo_tree_trigger
    BEFORE DELETE
    ON todo_node
    FOR EACH ROW
EXECUTE FUNCTION rebuild_todo_tree();