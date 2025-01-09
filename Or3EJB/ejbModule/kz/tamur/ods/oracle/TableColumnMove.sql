declare
-- input data
  type round_columns IS TABLE OF NUMBER;
  in_r_columns round_columns;
  in_OWNER VARCHAR2(32 char);
  in_TABLE_NAME VARCHAR2(32 char);
--. input data
  sql_line VARCHAR2(20000 BYTE);
  table_column_create_line VARCHAR2(512 char);
  create_table_sql  VARCHAR2(32767 char);
  insert_table_sql  VARCHAR2(32767 char);
  type varchar2_ar is table of varchar2(4096) index by binary_integer;
  out_columns varchar2_ar;
  re_out_columns varchar2_ar;
  indexes_line varchar2_ar;
  in_TABLE_TMP VARCHAR2(32 char) := 'TMP';
BEGIN
-- input data
  in_r_columns := round_columns(/**in_r_columns**/);
  select user into in_OWNER from dual;
  in_TABLE_NAME := /**in_TABLE_NAME**/;
--. input data

-- errors
  --если число переданых чисел не равно числу колонок в таблице
  DECLARE
    count_columns NUMBER;
  begin
    select max(COLUMN_ID)
      into count_columns
      FROM DBA_TAB_COLUMNS 
      where owner=in_OWNER and table_name=in_TABLE_NAME;
    if (in_r_columns.count <> count_columns) then
      raise_application_error(-20001,'The number of columns does not match the table. '
      ||chr(10)||'expected: '||count_columns||', received: '||in_r_columns.count);
    end if;
  end;

  --если есть повторяющиеся значения или "левые"
  DECLARE
    in_sum number := 0;
    out_sum number := 0;
  begin
    for i in 1..in_r_columns.count loop
      in_sum := in_sum + in_r_columns(i);
    end loop;
    select sum(COLUMN_ID)
      into out_sum
      FROM DBA_TAB_COLUMNS 
      where owner=in_OWNER and table_name=in_TABLE_NAME;
    if (in_sum <> out_sum) then
      raise_application_error(-20002,'Incorrect sequence of columns.');
    end if;
  end;
--. errors

  DECLARE
  CURSOR tmpcol_cur IS
    SELECT 
      COLUMN_NAME, DATA_TYPE, DATA_LENGTH, DATA_SCALE,
      NULLABLE, COLUMN_ID, DEFAULT_LENGTH, CHAR_LENGTH, CHAR_USED, DATA_PRECISION
    FROM DBA_TAB_COLUMNS 
    where owner=in_OWNER and table_name=in_TABLE_NAME;
  BEGIN
    FOR tmpcol_rec in tmpcol_cur LOOP
      table_column_create_line := '"' || tmpcol_rec.COLUMN_NAME || '" ' 
      || tmpcol_rec.DATA_TYPE;
      
      IF (INSTR(tmpcol_rec.DATA_TYPE, 'TIMESTAMP') = 0 and INSTR(tmpcol_rec.DATA_TYPE, 'INTERVAL') = 0) then
        IF (INSTR(tmpcol_rec.DATA_TYPE, 'RAW') = 1 or INSTR(tmpcol_rec.DATA_TYPE, 'UROWID') = 1) then
          table_column_create_line := table_column_create_line || '(' || tmpcol_rec.DATA_LENGTH || ')';
        elsif (tmpcol_rec.CHAR_USED = 'C') then
          table_column_create_line := table_column_create_line || '(' || tmpcol_rec.CHAR_LENGTH || ')';
        elsif (tmpcol_rec.CHAR_USED = 'B') then
          table_column_create_line := table_column_create_line || '(' || tmpcol_rec.CHAR_LENGTH || ' BYTE)';
        else 
          IF (tmpcol_rec.DATA_PRECISION is not null) then
            table_column_create_line := table_column_create_line || '(' || tmpcol_rec.DATA_PRECISION;
            IF (tmpcol_rec.DATA_SCALE is not null) then
              table_column_create_line := table_column_create_line || ',' || tmpcol_rec.DATA_SCALE;
            END IF;
            table_column_create_line := table_column_create_line || ')';
          elsif (tmpcol_rec.DATA_SCALE is not null) then
            table_column_create_line := table_column_create_line || '(*,' || tmpcol_rec.DATA_SCALE || ')';
          END IF;
        END IF;
      END IF;
      if (tmpcol_rec.DEFAULT_LENGTH is not null) then
        DECLARE
          data_def VARCHAR2(2000 char);
        BEGIN
          select DATA_DEFAULT into data_def 
          FROM DBA_TAB_COLUMNS 
          where owner=in_OWNER and table_name=in_TABLE_NAME and COLUMN_NAME=tmpcol_rec.COLUMN_NAME and rownum <= 1;
          table_column_create_line := table_column_create_line || ' DEFAULT ' || data_def;
        END;
      END IF;
      if (tmpcol_rec.NULLABLE = 'N') then
        table_column_create_line := table_column_create_line || ' NOT NULL ENABLE';
      end if;
      out_columns(tmpcol_rec.COLUMN_ID) := table_column_create_line;
      insert_table_sql := insert_table_sql ||',"'|| tmpcol_rec.COLUMN_NAME||'"';
    END LOOP;
  END;
  
  --перестановка столбцов с новой последовательностью.
  for i in in_r_columns.FIRST..in_r_columns.LAST loop
       re_out_columns(i) := out_columns(in_r_columns(i));
  end loop;
  
  create_table_sql := 'CREATE TABLE "'||in_OWNER||'"."'||in_TABLE_TMP||'" (	'||chr(10);
  create_table_sql := create_table_sql||re_out_columns(1);
  for i in 2..re_out_columns.count loop
       create_table_sql := create_table_sql||', '||chr(10)||re_out_columns(i);
  end loop;
  
-- keys and constraints
  DECLARE
    CURSOR cl_cur3 IS
      select OWNER, CONSTRAINT_NAME, CONSTRAINT_TYPE, TABLE_NAME, SEARCH_CONDITION, R_OWNER, R_CONSTRAINT_NAME, DELETE_RULE, STATUS, 
      DEFERRABLE, DEFERRED, VALIDATED, GENERATED, BAD, RELY, LAST_CHANGE, INDEX_OWNER, INDEX_NAME, INVALID, VIEW_RELATED
      from user_constraints where TABLE_NAME=in_TABLE_NAME and OWNER=in_OWNER and STATUS='ENABLED';
    
    col_name VARCHAR2(16 BYTE);
    tab_name VARCHAR2(16 BYTE);
  BEGIN
    FOR cl_rec in cl_cur3 LOOP
      --PRIMARY KEY
      if (cl_rec.CONSTRAINT_TYPE = 'P') then
        DECLARE
          P_key_line VARCHAR2(512 char) := ', '||chr(10)||'PRIMARY KEY (';
          bool_is_one number(1) := 0;
          CURSOR cl_index_ct IS
            select COLUMN_NAME 
              from ALL_CONS_COLUMNS 
              where owner=in_OWNER and table_name=in_TABLE_NAME and constraint_name=cl_rec.constraint_name ORDER BY POSITION;
        begin
          FOR cl_index_rec in cl_index_ct LOOP
            if (bool_is_one = 1) then 
              P_key_line := P_key_line || ', ';
            else
              bool_is_one := 1;
            end if;
            P_key_line := P_key_line ||'"'||cl_index_rec.COLUMN_NAME||'"';
          END LOOP;
          P_key_line := P_key_line || ')';
          create_table_sql := create_table_sql || P_key_line;
        end;
        
      --UNIQUE
      elsif (cl_rec.CONSTRAINT_TYPE = 'U') then
        DECLARE
          P_key_line VARCHAR2(512 char) := ', '||chr(10)||'UNIQUE (';
          bool_is_one number(1) := 0;
          CURSOR cl_index_ct IS
            select COLUMN_NAME 
              from ALL_CONS_COLUMNS 
              where owner=in_OWNER and table_name=in_TABLE_NAME and constraint_name=cl_rec.INDEX_NAME ORDER BY POSITION;
        begin
          FOR cl_index_rec in cl_index_ct LOOP
            if (bool_is_one = 1) then 
              P_key_line := P_key_line || ', ';
            else
              bool_is_one := 1;
            end if;
            P_key_line := P_key_line ||'"'||cl_index_rec.COLUMN_NAME||'"';
          END LOOP;
          P_key_line := P_key_line || ')';
          create_table_sql := create_table_sql || P_key_line;
        end;
        
      --FOREIGN KEY
      elsif (cl_rec.CONSTRAINT_TYPE = 'R') then
        DECLARE
          P_key_line VARCHAR2(512 char) := ', '||chr(10)||'FOREIGN KEY (';
          bool_is_one number(1) := 0;
          CURSOR cl_index_ct IS
            select COLUMN_NAME 
              from ALL_CONS_COLUMNS 
              where owner=in_OWNER and table_name=in_TABLE_NAME and constraint_name=cl_rec.CONSTRAINT_NAME ORDER BY POSITION;
        begin
          FOR cl_index_rec in cl_index_ct LOOP
            if (bool_is_one = 1) then 
              P_key_line := P_key_line || ', ';
            else
              bool_is_one := 1;
            end if;
            P_key_line := P_key_line ||'"'||cl_index_rec.COLUMN_NAME||'"';
          END LOOP;
          P_key_line := P_key_line || ')';
          DECLARE
            P_key_line_to VARCHAR2(512 char);
            OWNER_to VARCHAR2(512 char);
            TABLE_NAME_to VARCHAR2(512 char);
            bool_is_one number(1) := 0;
            CURSOR cl_index_ct IS
              select OWNER, TABLE_NAME, COLUMN_NAME 
                from ALL_CONS_COLUMNS 
                where constraint_name=cl_rec.R_CONSTRAINT_NAME ORDER BY POSITION;
          begin
            FOR cl_index_rec in cl_index_ct LOOP
              OWNER_to := cl_index_rec.OWNER;
              TABLE_NAME_to := cl_index_rec.TABLE_NAME;
              if (bool_is_one = 1) then 
                P_key_line_to := P_key_line_to || ', ';
              else
                bool_is_one := 1;
              end if;
              P_key_line_to := P_key_line_to ||'"'||cl_index_rec.COLUMN_NAME||'"';
            END LOOP;
            P_key_line := P_key_line || ' REFERENCES "'||OWNER_to||'"."'||TABLE_NAME_to||'" ('|| P_key_line_to || ')';
          end;
          create_table_sql := create_table_sql || P_key_line;
        end;
      end if;
    end loop;
  END;
--. keys and constraints

  create_table_sql := create_table_sql || chr(10)|| ')';
  insert_table_sql := 'INSERT INTO "'||in_OWNER||'"."'||in_TABLE_TMP||'" ('||SUBSTR(insert_table_sql, 2) || ') select '||SUBSTR(insert_table_sql, 2) || ' from "'||in_OWNER||'"."'||in_TABLE_NAME||'"';
  
  BEGIN
    --DBMS_OUTPUT.put_line('drop table: '||'DROP TABLE "'||in_OWNER||'"."'||in_TABLE_TMP||'" cascade constraints PURGE');
    EXECUTE IMMEDIATE 'DROP TABLE "'||in_OWNER||'"."'||in_TABLE_TMP||'" cascade constraints PURGE';
  EXCEPTION
    WHEN OTHERS THEN
      IF SQLCODE != -942 THEN --"table or view does not exist"
         RAISE;
      END IF;
  END;
  --DBMS_OUTPUT.put_line('create table: '||create_table_sql);
  EXECUTE IMMEDIATE create_table_sql; 
  --DBMS_OUTPUT.put_line('insert table: '||insert_table_sql);
  EXECUTE IMMEDIATE insert_table_sql; 
  
-- comments
  DECLARE
    table_comment VARCHAR2(4096 char) := '';
    CURSOR cl_comments IS
      select COLUMN_NAME, COMMENTS 
      from DBA_COL_COMMENTS 
      where owner=in_OWNER and table_name=in_TABLE_NAME and comments is not null;
  BEGIN
  
    BEGIN
      select COMMENTS into table_comment from DBA_TAB_COMMENTS where owner=in_OWNER and table_name=in_TABLE_NAME and TABLE_TYPE='TABLE' and comments is not null and rownum <= 1;
      if (table_comment is not null) then
        --dbms_output.put_line('comment table: COMMENT ON TABLE "'||in_OWNER||'"."'||in_TABLE_TMP||'"  IS '''||table_comment||'''');
        EXECUTE IMMEDIATE ('COMMENT ON TABLE "'||in_OWNER||'"."'||in_TABLE_TMP||'"  IS '''||table_comment||'''');
      end if;
    EXCEPTION
      WHEN OTHERS THEN
      IF SQLCODE != 100 THEN --"no data found"
        RAISE;
      END IF;
    END;

    FOR rec_comments in cl_comments LOOP
      --dbms_output.put_line('comments column: COMMENT ON COLUMN "'||in_OWNER||'"."'||in_TABLE_TMP||'"."'||rec_comments.COLUMN_NAME||'" IS '''||rec_comments.COMMENTS||'''');
      EXECUTE IMMEDIATE ('COMMENT ON COLUMN "'||in_OWNER||'"."'||in_TABLE_TMP||'"."'||rec_comments.COLUMN_NAME||'" IS '''||rec_comments.COMMENTS||''''); 
    end loop;
  END;
--. comments
  
-- external keys
  DECLARE
    external_FK_add varchar2_ar;
    external_FK_del varchar2_ar;
    num number := 1;
    external_FK varchar2_ar;
    CURSOR cl_index_ct IS
      select CONSTRAINT_NAME, INDEX_NAME, DELETE_RULE
      from user_constraints where owner=in_OWNER and table_name=in_TABLE_NAME and CONSTRAINT_TYPE='P' and STATUS='ENABLED';
  BEGIN
    external_FK(num) := '';
    FOR a_rec in cl_index_ct LOOP
      DECLARE
        OWNER_to VARCHAR2(512 char);
        TABLE_NAME_to VARCHAR2(512 char);
        CURSOR cl_index_ct IS
          select OWNER, CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME 
          from ALL_CONS_COLUMNS where constraint_name=a_rec.INDEX_NAME ORDER BY POSITION;
      BEGIN
        FOR b_rec in cl_index_ct LOOP
          OWNER_to := b_rec.OWNER;
          TABLE_NAME_to := b_rec.TABLE_NAME;
          external_FK(num) := external_FK(num)||','||b_rec.COLUMN_NAME;
        end loop;
        external_FK(num) := ' REFERENCES "'||OWNER_to||'"."'||in_TABLE_TMP||'" ('||SUBSTR(external_FK(num), 2)||')';
      END;
      
      DECLARE
        external_FK_line_num number := 1;
        CURSOR cl_index_ct IS
          select CONSTRAINT_NAME, TABLE_NAME, DELETE_RULE
          from user_constraints where owner=in_OWNER and r_constraint_name=a_rec.CONSTRAINT_NAME;
      BEGIN
        FOR c_rec in cl_index_ct LOOP
          DECLARE
            FK_key_line VARCHAR2(512 char) := '';
            OWNER_to VARCHAR2(512 char);
            TABLE_NAME_to VARCHAR2(512 char);
            CURSOR cl_index_ct IS
              select TABLE_NAME, COLUMN_NAME, OWNER
              from ALL_CONS_COLUMNS where owner=in_OWNER and constraint_name=c_rec.CONSTRAINT_NAME ORDER BY POSITION;
          BEGIN
            FOR d_rec in cl_index_ct LOOP
              OWNER_to := d_rec.OWNER;
              TABLE_NAME_to := d_rec.TABLE_NAME;
              FK_key_line := FK_key_line || ',' || d_rec.COLUMN_NAME;
            end loop;
            external_FK_add(external_FK_line_num) := 'ALTER TABLE "'||OWNER_to||'"."'||TABLE_NAME_to||'" ADD CONSTRAINT '||c_rec.CONSTRAINT_NAME||
              ' FOREIGN KEY ('||SUBSTR(FK_key_line,2)||')'||external_FK(num);
            if (c_rec.DELETE_RULE = 'CASCADE') then
              external_FK_add(external_FK_line_num) := external_FK_add(external_FK_line_num)||' ON DELETE CASCADE ENABLE';
            elsif (c_rec.DELETE_RULE = 'NO ACTION') then
              external_FK_add(external_FK_line_num) := external_FK_add(external_FK_line_num)||' ENABLE';
            elsif (c_rec.DELETE_RULE = 'SET NULL') then
              external_FK_add(external_FK_line_num) := external_FK_add(external_FK_line_num)||' ON DELETE SET NULL ENABLE';
            end if;
            external_FK_del(external_FK_line_num) := 'ALTER TABLE "'||OWNER_to||'"."'||TABLE_NAME_to||'" DROP CONSTRAINT '||c_rec.CONSTRAINT_NAME;
          END;
          external_FK_line_num := external_FK_line_num + 1;
        end loop;
      END;
      num := num + 1;
    end loop;
    
    if (external_FK_del.count <> 0 or external_FK_add.count <> 0) then
      for i in external_FK_del.FIRST..external_FK_del.LAST loop
        --dbms_output.put_line('external_FK_del: '||external_FK_del(i));
        EXECUTE IMMEDIATE external_FK_del(i);
      end loop;
      
      for i in 1..external_FK_add.count loop
        --dbms_output.put_line('external_FK_add: '||external_FK_add(i));
        EXECUTE IMMEDIATE external_FK_add(i);
      end loop;
    end if;
    
  END;
--. external keys

-- indexes
  DECLARE
    num number := 1;
    column_name VARCHAR2(512 char) := '';
    CURSOR cl_indexes IS
      select INDEX_NAME, STATUS, INDEX_TYPE
      from all_indexes 
      where TABLE_OWNER=in_OWNER and table_name=in_TABLE_NAME and UNIQUENESS='NONUNIQUE';
  BEGIN
    FOR rec_indexes in cl_indexes LOOP 
      DECLARE
        tmp_name VARCHAR2(512 char) := '';
        CURSOR cl_indexes_cn IS
          select INDEX_OWNER, INDEX_NAME, TABLE_OWNER, TABLE_NAME, COLUMN_NAME, COLUMN_POSITION, COLUMN_LENGTH, CHAR_LENGTH, DESCEND 
          from all_ind_columns 
          where TABLE_OWNER=in_OWNER and table_name=in_TABLE_NAME and INDEX_NAME=rec_indexes.INDEX_NAME;
      BEGIN
        column_name := '';
        FOR rec_indexes_cn in cl_indexes_cn LOOP
          if (rec_indexes.INDEX_TYPE = 'NORMAL') then
            tmp_name := '"'||rec_indexes_cn.COLUMN_NAME||'"';
          else
            select COLUMN_EXPRESSION 
              into tmp_name
              from user_ind_expressions 
              where index_name=rec_indexes.INDEX_NAME and COLUMN_POSITION=rec_indexes_cn.COLUMN_POSITION and rownum <= 1;
          end if;
          column_name := column_name||','||tmp_name;
          if (rec_indexes_cn.DESCEND = 'DESC') then
            column_name := column_name||' '||rec_indexes_cn.DESCEND;
          end if;
        end loop;
        indexes_line(num) := 'CREATE INDEX "'||in_OWNER||'"."'||rec_indexes.INDEX_NAME||'" ON "'||in_OWNER||'"."'||in_TABLE_TMP||'" ('||SUBSTR(column_name, 2)||')';
        --dbms_output.put_line(indexes_line(num));
      END;
      num := num + 1;
    end loop;
  END;
--. indexes

-- trigger
  declare
    line  VARCHAR2(32767 char);
    compare_line VARCHAR2(2000 char);
    end_title_triger int;
    pos_table_name int;
    CURSOR cl_trbody IS
      select TRIGGER_NAME, trigger_body 
      from DBA_TRIGGERS 
      where owner=in_OWNER and TABLE_NAME=in_TABLE_NAME and base_object_type='TABLE' and status='ENABLED';
  BEGIN
    FOR rec_trbody in cl_trbody LOOP
      select dbms_metadata.get_ddl('TRIGGER', rec_trbody.TRIGGER_NAME, user) into line from dual;
      end_title_triger := INSTR(line, 'BEGIN') - 1;
      line := SUBSTR(line, 0, end_title_triger);
      
      pos_table_name := INSTR(line, ' ON '||in_TABLE_NAME) + 3;
      
      compare_line := SUBSTR(line, 0, pos_table_name);
      compare_line := compare_line || in_TABLE_TMP;
      pos_table_name := pos_table_name + LENGTH(in_TABLE_NAME)+1;
      compare_line := compare_line || SUBSTR(line, pos_table_name, LENGTH(line));
      compare_line := compare_line || rec_trbody.trigger_body;
      
      EXECUTE IMMEDIATE 'drop trigger "'||in_OWNER||'"."'|| rec_trbody.TRIGGER_NAME ||'"';
      EXECUTE IMMEDIATE compare_line||chr(10)||'-- redesigned trigger script "TableColumnMove"'||chr(10);
    END LOOP;
  END;
--. trigger

  EXECUTE IMMEDIATE 'DROP TABLE "'||in_OWNER||'"."'||in_TABLE_NAME||'" cascade constraints PURGE';
  --востанавлием индексы
  if (indexes_line.count <> 0) then
    for i in indexes_line.FIRST..indexes_line.LAST loop
      --dbms_output.put_line('indexes: '||indexes_line(i));
      EXECUTE IMMEDIATE indexes_line(i);
    end loop;
  end if;
  EXECUTE IMMEDIATE 'ALTER TABLE "'||in_OWNER||'"."'||in_TABLE_tmp||'" RENAME TO "'||in_TABLE_NAME||'"';

  commit;
  
EXCEPTION     
  when others then
    --dbms_output.put_line(SQLCODE||'fail'||SQLERRM);
    rollback;
    raise_application_error(-20001, SQLERRM);
END;