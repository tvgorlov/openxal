CREATE TABLE MACHINE_MODEL.DEVICE_TYPES
  (
    ID                      NUMBER (*,0) NOT NULL ,
    DEVICE_TYPE             VARCHAR2 (4 BYTE) NOT NULL ,
    CREATED_BY              VARCHAR2 (30 BYTE) NOT NULL ,
    DATE_CREATED            DATE NOT NULL ,
    UPDATED_BY              VARCHAR2 (30 BYTE) ,
    DATE_UPDATED            DATE ,
    DEFAULT_SLICING_POS_CHK NUMBER (*,0)
  )
  PCTFREE 10 PCTUSED 40 TABLESPACE MACHINE_MODEL LOGGING STORAGE
  (
    INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
  ) ;
ALTER TABLE MACHINE_MODEL.DEVICE_TYPES ADD CONSTRAINT CKC_DEFAULT_SLICING_P_DEVICE_T CHECK ( DEFAULT_SLICING_POS_CHK IS NULL OR ( DEFAULT_SLICING_POS_CHK IN (0,1,2) )) ;
CREATE UNIQUE INDEX MACHINE_MODEL.AK_UK_DEVICE_TYPE_DEVICE_T ON MACHINE_MODEL.DEVICE_TYPES ( DEVICE_TYPE ASC ) TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE ( INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT ) LOGGING ;
CREATE UNIQUE INDEX MACHINE_MODEL.PK_DEVICE_TYPES ON MACHINE_MODEL.DEVICE_TYPES ( ID ASC ) TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE ( INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT ) LOGGING ;
CREATE BITMAP INDEX MACHINE_MODEL.BMP_DEVICE_TYPES_SLICEPOS ON MACHINE_MODEL.DEVICE_TYPES ( DEFAULT_SLICING_POS_CHK ASC ) TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE ( INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT ) NOLOGGING ;
  ALTER TABLE MACHINE_MODEL.DEVICE_TYPES ADD CONSTRAINT PK_DEVICE_TYPES PRIMARY KEY ( ID ) USING INDEX MACHINE_MODEL.PK_DEVICE_TYPES ;
  ALTER TABLE MACHINE_MODEL.DEVICE_TYPES ADD CONSTRAINT AK_UK_DEVICE_TYPE_DEVICE_T UNIQUE ( DEVICE_TYPE ) USING INDEX MACHINE_MODEL.AK_UK_DEVICE_TYPE_DEVICE_T ;
  GRANT
  SELECT ON MACHINE_MODEL.DEVICE_TYPES TO AIDAPROD ;
  GRANT
  SELECT ON MACHINE_MODEL.DEVICE_TYPES TO AIDADEV ;
  GRANT ALTER,
  DELETE, INDEX,
  INSERT,
  SELECT,
  UPDATE,
    REFERENCES,
    ON COMMIT REFRESH,
    QUERY REWRITE,
    DEBUG,
    FLASHBACK ON MACHINE_MODEL.DEVICE_TYPES TO LCLS_INFRASTRUCTURE ;
  GRANT
  SELECT ON MACHINE_MODEL.DEVICE_TYPES TO LCLS_READ ;
  CREATE TABLE MACHINE_MODEL.ELEMENT_MODELS
    (
      ID              NUMBER (*,0) NOT NULL ,
      RUNS_ID         NUMBER (*,0) ,
      CREATED_BY      VARCHAR2 (30 BYTE) NOT NULL ,
      DATE_CREATED    DATE NOT NULL ,
      UPDATED_BY      VARCHAR2 (30 BYTE) ,
      DATE_UPDATED    DATE ,
      ELEMENT_NAME    VARCHAR2 (60 BYTE) ,
      INDEX_SLICE_CHK NUMBER (*,0) ,
      ZPOS            NUMBER ,
      EK              NUMBER ,
      ALPHA_X         NUMBER ,
      ALPHA_Y         NUMBER ,
      BETA_X          NUMBER ,
      BETA_Y          NUMBER ,
      PSI_X           NUMBER ,
      PSI_Y           NUMBER ,
      ETA_X           NUMBER ,
      ETA_Y           NUMBER ,
      ETAP_X          NUMBER ,
      ETAP_Y          NUMBER ,
      R11             NUMBER ,
      R12             NUMBER ,
      R13             NUMBER ,
      R14             NUMBER ,
      R15             NUMBER ,
      R16             NUMBER ,
      R21             NUMBER ,
      R22             NUMBER ,
      R23             NUMBER ,
      R24             NUMBER ,
      R25             NUMBER ,
      R26             NUMBER ,
      R31             NUMBER ,
      R32             NUMBER ,
      R33             NUMBER ,
      R34             NUMBER ,
      R35             NUMBER ,
      R36             NUMBER ,
      R41             NUMBER ,
      R42             NUMBER ,
      R43             NUMBER ,
      R44             NUMBER ,
      R45             NUMBER ,
      R46             NUMBER ,
      R51             NUMBER ,
      R52             NUMBER ,
      R53             NUMBER ,
      R54             NUMBER ,
      R55             NUMBER ,
      R56             NUMBER ,
      R61             NUMBER ,
      R62             NUMBER ,
      R63             NUMBER ,
      R64             NUMBER ,
      R65             NUMBER ,
      R66             NUMBER ,
      LEFF            NUMBER ,
      SLEFF           NUMBER ,
      ORDINAL         NUMBER ,
      SUML            NUMBER
    )
    PCTFREE 10 PCTUSED 40 TABLESPACE MACHINE_MODEL LOGGING STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    ) ;
  ALTER TABLE MACHINE_MODEL.ELEMENT_MODELS ADD CONSTRAINT CKC_INDEX_SLICE_CHK_ELEMENT_ CHECK ( INDEX_SLICE_CHK IS NULL OR ( INDEX_SLICE_CHK IN (0,1,2) )) ;
  CREATE INDEX MACHINE_MODEL.FK_RUN_ELEM_MODELS ON MACHINE_MODEL.ELEMENT_MODELS
    (
      RUNS_ID ASC
    )
    TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    )
    LOGGING ;
  -- Error - Index FK_ELEM_MODEL has no columns
  CREATE INDEX MACHINE_MODEL.IDX_ELEMENT_MODELS_ORDINAL ON MACHINE_MODEL.ELEMENT_MODELS
    (
      ORDINAL ASC
    )
    TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    )
    LOGGING ;
CREATE UNIQUE INDEX MACHINE_MODEL.PK_ELEMENT_MODELS ON MACHINE_MODEL.ELEMENT_MODELS
  (
    ID ASC
  )
  TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
  (
    INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
  )
  LOGGING ;
  ALTER TABLE MACHINE_MODEL.ELEMENT_MODELS ADD CONSTRAINT PK_ELEMENT_MODELS PRIMARY KEY ( ID ) USING INDEX MACHINE_MODEL.PK_ELEMENT_MODELS ;
  GRANT
  SELECT ON MACHINE_MODEL.ELEMENT_MODELS TO AIDAPROD ;
  GRANT
  SELECT ON MACHINE_MODEL.ELEMENT_MODELS TO AIDADEV ;
  GRANT ALTER,
  DELETE, INDEX,
  INSERT,
  SELECT,
  UPDATE,
    REFERENCES,
    ON COMMIT REFRESH,
    QUERY REWRITE,
    DEBUG,
    FLASHBACK ON MACHINE_MODEL.ELEMENT_MODELS TO LCLS_INFRASTRUCTURE ;
  GRANT
  SELECT ON MACHINE_MODEL.ELEMENT_MODELS TO LCLS_READ ;
  CREATE TABLE MACHINE_MODEL.GOLD
    (
      ID           NUMBER (*,0) NOT NULL ,
      RUNS_ID      NUMBER (*,0) ,
      COMMENTS     VARCHAR2 (200 BYTE) ,
      CREATED_BY   VARCHAR2 (30 BYTE) NOT NULL ,
      DATE_CREATED DATE NOT NULL
    )
    PCTFREE 10 PCTUSED 40 TABLESPACE MACHINE_MODEL LOGGING STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    ) ;
CREATE UNIQUE INDEX MACHINE_MODEL.PK_GOLD ON MACHINE_MODEL.GOLD
  (
    ID ASC
  )
  TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
  (
    INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
  )
  LOGGING ;
  CREATE INDEX MACHINE_MODEL.FK_GOLD_RUNS ON MACHINE_MODEL.GOLD
    (
      RUNS_ID ASC
    )
    TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    )
    LOGGING ;
  ALTER TABLE MACHINE_MODEL.GOLD ADD CONSTRAINT PK_GOLD PRIMARY KEY ( ID ) USING INDEX MACHINE_MODEL.PK_GOLD ;
  GRANT
  SELECT ON MACHINE_MODEL.GOLD TO AIDAPROD ;
  GRANT
  SELECT ON MACHINE_MODEL.GOLD TO AIDADEV ;
  GRANT ALTER,
  DELETE, INDEX,
  INSERT,
  SELECT,
  UPDATE,
    REFERENCES,
    ON COMMIT REFRESH,
    QUERY REWRITE,
    DEBUG,
    FLASHBACK ON MACHINE_MODEL.GOLD TO LCLS_INFRASTRUCTURE ;
  GRANT
  SELECT ON MACHINE_MODEL.GOLD TO LCLS_READ ;
  CREATE TABLE MACHINE_MODEL.MODEL_DEVICES
    (
      ID              NUMBER (*,0) NOT NULL ,
      RUNS_ID         NUMBER (*,0) ,
      DEVICE_TYPES_ID NUMBER (*,0) ,
      CREATED_BY      VARCHAR2 (30 BYTE) NOT NULL ,
      DATE_CREATED    DATE NOT NULL ,
      DEVICE_PROPERTY VARCHAR2 (30 BYTE) ,
      DEVICE_VALUE    NUMBER
    )
    PCTFREE 10 PCTUSED 40 TABLESPACE MACHINE_MODEL LOGGING STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    ) ;
  ALTER TABLE MACHINE_MODEL.MODEL_DEVICES ADD CONSTRAINT CKC_MODEL_DEVICES_DEV_PROP CHECK ( DEVICE_PROPERTY IS NULL OR ( DEVICE_PROPERTY IN ('B','BACT','BDES','A','P','ADES','PDES') )) ;
CREATE UNIQUE INDEX MACHINE_MODEL.PK_MODEL_DEVICES ON MACHINE_MODEL.MODEL_DEVICES ( ID ASC ) TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE ( INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT ) LOGGING ;
  CREATE INDEX MACHINE_MODEL.FK_TYPE_DEVICE ON MACHINE_MODEL.MODEL_DEVICES
    (
      DEVICE_TYPES_ID ASC
    )
    TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    )
    LOGGING ;
  CREATE INDEX MACHINE_MODEL.FK_RUN_DEVICE ON MACHINE_MODEL.MODEL_DEVICES
    (
      RUNS_ID ASC
    )
    TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    )
    LOGGING ;
  -- Error - Index FK_ELEM_DEVICE has no columns
  ALTER TABLE MACHINE_MODEL.MODEL_DEVICES ADD CONSTRAINT PK_MODEL_DEVICES PRIMARY KEY ( ID ) USING INDEX MACHINE_MODEL.PK_MODEL_DEVICES ;
  GRANT
  SELECT ON MACHINE_MODEL.MODEL_DEVICES TO AIDAPROD ;
  GRANT
  SELECT ON MACHINE_MODEL.MODEL_DEVICES TO AIDADEV ;
  GRANT ALTER,
  DELETE, INDEX,
  INSERT,
  SELECT,
  UPDATE,
    REFERENCES,
    ON COMMIT REFRESH,
    QUERY REWRITE,
    DEBUG,
    FLASHBACK ON MACHINE_MODEL.MODEL_DEVICES TO LCLS_INFRASTRUCTURE ;
  GRANT
  SELECT ON MACHINE_MODEL.MODEL_DEVICES TO LCLS_READ ;
  CREATE TABLE MACHINE_MODEL.MODEL_LINES
    (
      ID              NUMBER (*,0) NOT NULL ,
      MODEL_LINE_NAME VARCHAR2 (60 BYTE) NOT NULL ,
      CREATED_BY      VARCHAR2 (30 BYTE) NOT NULL ,
      DATE_CREATED    DATE NOT NULL ,
      UPDATED_BY      VARCHAR2 (30 BYTE) ,
      DATE_UPDATED    DATE
    )
    PCTFREE 10 PCTUSED 40 TABLESPACE MACHINE_MODEL LOGGING STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    ) ;
CREATE UNIQUE INDEX MACHINE_MODEL.PK_MODEL_LINES ON MACHINE_MODEL.MODEL_LINES
  (
    ID ASC
  )
  TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
  (
    INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
  )
  LOGGING ;
CREATE UNIQUE INDEX MACHINE_MODEL.AK_UK_MODLINES_NAME ON MACHINE_MODEL.MODEL_LINES
  (
    MODEL_LINE_NAME ASC
  )
  TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
  (
    INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
  )
  LOGGING ;
  ALTER TABLE MACHINE_MODEL.MODEL_LINES ADD CONSTRAINT PK_MODEL_LINES PRIMARY KEY ( ID ) USING INDEX MACHINE_MODEL.PK_MODEL_LINES ;
  ALTER TABLE MACHINE_MODEL.MODEL_LINES ADD CONSTRAINT AK_UK_MODLINES_NAME UNIQUE ( MODEL_LINE_NAME ) USING INDEX MACHINE_MODEL.AK_UK_MODLINES_NAME ;
  GRANT
  SELECT ON MACHINE_MODEL.MODEL_LINES TO AIDAPROD ;
  GRANT
  SELECT ON MACHINE_MODEL.MODEL_LINES TO AIDADEV ;
  GRANT ALTER,
  DELETE, INDEX,
  INSERT,
  SELECT,
  UPDATE,
    REFERENCES,
    ON COMMIT REFRESH,
    QUERY REWRITE,
    DEBUG,
    FLASHBACK ON MACHINE_MODEL.MODEL_LINES TO LCLS_INFRASTRUCTURE ;
  CREATE TABLE MACHINE_MODEL.MODEL_MODES
    (
      ID             NUMBER (*,0) NOT NULL ,
      MODEL_LINES_ID NUMBER (*,0) ,
      CREATED_BY     VARCHAR2 (30 BYTE) NOT NULL ,
      DATE_CREATED   DATE NOT NULL ,
      UPDATED_BY     VARCHAR2 (30 BYTE) ,
      DATE_UPDATED   DATE
    )
    PCTFREE 10 PCTUSED 40 TABLESPACE MACHINE_MODEL LOGGING STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    ) ;
CREATE UNIQUE INDEX MACHINE_MODEL.PK_MODEL_MODES ON MACHINE_MODEL.MODEL_MODES
  (
    ID ASC
  )
  TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
  (
    INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
  )
  LOGGING ;
  CREATE INDEX MACHINE_MODEL.FK_MODMODE_MODLINE ON MACHINE_MODEL.MODEL_MODES
    (
      MODEL_LINES_ID ASC
    )
    TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    )
    LOGGING ;
  -- Error - Index FK_MODMODE_IC has no columns
  ALTER TABLE MACHINE_MODEL.MODEL_MODES ADD CONSTRAINT PK_MODEL_MODES PRIMARY KEY ( ID ) USING INDEX MACHINE_MODEL.PK_MODEL_MODES ;
  GRANT
  SELECT ON MACHINE_MODEL.MODEL_MODES TO AIDAPROD ;
  GRANT
  SELECT ON MACHINE_MODEL.MODEL_MODES TO AIDADEV ;
  GRANT ALTER,
  DELETE, INDEX,
  INSERT,
  SELECT,
  UPDATE,
    REFERENCES,
    ON COMMIT REFRESH,
    QUERY REWRITE,
    DEBUG,
    FLASHBACK ON MACHINE_MODEL.MODEL_MODES TO LCLS_INFRASTRUCTURE ;
  CREATE TABLE MACHINE_MODEL.RUNS
    (
      ID               NUMBER (*,0) NOT NULL ,
      CREATED_BY       VARCHAR2 (30 BYTE) NOT NULL ,
      DATE_CREATED     DATE NOT NULL ,
      RUN_SOURCE_CHK   VARCHAR2 (60 BYTE) ,
      RUN_ELEMENT_DATE DATE ,
      RUN_DEVICE_DATE  DATE ,
      COMMENTS         VARCHAR2 (200 BYTE) ,
      MODEL_MODES_ID   NUMBER (*,0)
    )
    PCTFREE 10 PCTUSED 40 TABLESPACE MACHINE_MODEL LOGGING STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    ) ;
  ALTER TABLE MACHINE_MODEL.RUNS ADD CONSTRAINT CKC_RUN_SOURCE_CHK_RUNS CHECK ( RUN_SOURCE_CHK IS NULL OR ( RUN_SOURCE_CHK IN ('DESIGN','EXTANT') )) ;
  CREATE INDEX MACHINE_MODEL.IDX_RUNS_RUN_SOURCE_CHK ON MACHINE_MODEL.RUNS
    (
      RUN_SOURCE_CHK ASC
    )
    TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
    (
      INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
    )
    NOLOGGING ;
CREATE UNIQUE INDEX MACHINE_MODEL.PK_RUNS ON MACHINE_MODEL.RUNS
  (
    ID ASC
  )
  TABLESPACE MACHINE_MODEL PCTFREE 10 STORAGE
  (
    INITIAL 65536 NEXT 1048576 PCTINCREASE 0 MINEXTENTS 1 MAXEXTENTS 2147483645 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT
  )
  LOGGING ;
  ALTER TABLE MACHINE_MODEL.RUNS ADD CONSTRAINT PK_RUNS PRIMARY KEY ( ID ) USING INDEX MACHINE_MODEL.PK_RUNS ;
  GRANT
  SELECT ON MACHINE_MODEL.RUNS TO AIDAPROD ;
  GRANT
  SELECT ON MACHINE_MODEL.RUNS TO AIDADEV ;
  GRANT ALTER,
  DELETE, INDEX,
  INSERT,
  SELECT,
  UPDATE,
    REFERENCES,
    ON COMMIT REFRESH,
    QUERY REWRITE,
    DEBUG,
    FLASHBACK ON MACHINE_MODEL.RUNS TO LCLS_INFRASTRUCTURE ;
  GRANT
  SELECT ON MACHINE_MODEL.RUNS TO LCLS_READ ;
  ALTER TABLE MACHINE_MODEL.ELEMENT_MODELS ADD CONSTRAINT FK_ELEMENT__FK_RUN_EL_RUNS FOREIGN KEY ( RUNS_ID ) REFERENCES MACHINE_MODEL.RUNS ( ID ) DEFERRABLE INITIALLY DEFERRED ;
  ALTER TABLE MACHINE_MODEL.GOLD ADD CONSTRAINT FK_GOLD_RUNS FOREIGN KEY ( RUNS_ID ) REFERENCES MACHINE_MODEL.RUNS ( ID ) DEFERRABLE INITIALLY DEFERRED ;
  ALTER TABLE MACHINE_MODEL.MODEL_DEVICES ADD CONSTRAINT FK_MODEL_DE_FK_RUN_DE_RUNS FOREIGN KEY ( RUNS_ID ) REFERENCES MACHINE_MODEL.RUNS ( ID ) DEFERRABLE INITIALLY DEFERRED ;
  ALTER TABLE MACHINE_MODEL.MODEL_DEVICES ADD CONSTRAINT FK_MODEL_DE_FK_TYPE_D_DEVICE_T FOREIGN KEY ( DEVICE_TYPES_ID ) REFERENCES MACHINE_MODEL.DEVICE_TYPES ( ID ) DEFERRABLE INITIALLY DEFERRED ;
  ALTER TABLE MACHINE_MODEL.MODEL_MODES ADD CONSTRAINT FK_MODMODE_MODLINE FOREIGN KEY ( MODEL_LINES_ID ) REFERENCES MACHINE_MODEL.MODEL_LINES ( ID ) DEFERRABLE INITIALLY DEFERRED ;
  ALTER TABLE MACHINE_MODEL.RUNS ADD CONSTRAINT FK_RUNS_MODMODE FOREIGN KEY ( MODEL_MODES_ID ) REFERENCES MACHINE_MODEL.MODEL_MODES ( ID ) DEFERRABLE INITIALLY DEFERRED ;
CREATE OR REPLACE TRIGGER MACHINE_MODEL.DEVICE_TYPES_BIUDR_TRG BEFORE
  INSERT OR
  UPDATE OR
  DELETE ON MACHINE_MODEL.DEVICE_TYPES FOR EACH ROW DECLARE
    -- PRAGMA AUTONOMOUS_TRANSACTION;
    V_PROC VARCHAR2(30);
  V_ERRMSG VARCHAR2(300);
  V_USER   VARCHAR2(100) := UPPER(NVL(V('APP_USER'),USER));
  -- INITIALIZE PACKAGE VARIABLES
  -- VX              VARCHAR2(1) := ONLINE_MODEL_PKG.PKG_ALLOW_DELETE;
  PK_CHG_ERROR EXCEPTION;
  DEL_ERROR    EXCEPTION;
  BEGIN
    V_PROC   := 'DEVICE_TYPES_BIUDR_TRG';
    V_ERRMSG := NULL;
    IF INSERTING OR UPDATING THEN
      :new.DEVICE_TYPE := SUBSTR(UPPER(TRIM(:new.DEVICE_TYPE)),1,4);
    END IF;
    IF INSERTING THEN
      IF ( :new.ID IS NULL ) THEN
        SELECT DEVICE_TYPES_SEQ.NEXTVAL INTO :new.ID FROM DUAL;
      END IF;
      IF ( :new.CREATED_BY IS NULL ) THEN
        :new.CREATED_BY    := V_USER;
      END IF;
      IF ( :new.DATE_CREATED IS NULL ) THEN
        :new.DATE_CREATED    := SYSDATE;
      END IF;
      :new.UPDATED_BY   := NULL;
      :new.DATE_UPDATED := NULL;
    ELSIF UPDATING THEN
      IF (:new.ID IS NOT NULL AND :new.ID <> :old.ID ) THEN
        V_ERRMSG  := 'ERROR: '||V_PROC||': ATTEMPTING TO CHANGE PRIMARY KEY '||TO_CHAR(:old.ID)||' DURING UPDATE OF DEVICE_TYPES.';
        RAISE PK_CHG_ERROR;
      END IF;
      :new.UPDATED_BY   := V_USER;
      :new.DATE_UPDATED := SYSDATE;
    ELSIF DELETING THEN
      -- DELETE CHILD TABLE ROWS
      BEGIN
        DELETE FROM MODEL_DEVICES WHERE DEVICE_TYPES_ID = :old.ID;
      EXCEPTION
      WHEN OTHERS THEN
        V_ERRMSG := SUBSTR('ERROR ('||V_PROC||'): DELETE FROM MODEL_DEVICES DURING DELETE FROM DEVICE_TYPES FOR ID='|| TO_CHAR(:old.ID)||' => '||SQLERRM,1,300);
        RAISE DEL_ERROR;
      END;
    END IF; -- IF INSERTING THEN
    -- COMMIT;
  EXCEPTION
  WHEN PK_CHG_ERROR THEN
    RAISE_APPLICATION_ERROR(-20010, V_ERRMSG);
  WHEN DEL_ERROR THEN
    RAISE_APPLICATION_ERROR(-20020, V_ERRMSG);
  WHEN OTHERS THEN
    V_ERRMSG := SUBSTR('ERROR: '||V_PROC||': '||SQLERRM,1,300);
    RAISE_APPLICATION_ERROR(-20030, V_ERRMSG);
  END;
  /
CREATE OR REPLACE TRIGGER MACHINE_MODEL.ELEMENT_MODELS_BIUDR_TRG BEFORE
  INSERT OR
  UPDATE OR
  DELETE ON MACHINE_MODEL.ELEMENT_MODELS FOR EACH ROW DECLARE
    -- PRAGMA AUTONOMOUS_TRANSACTION;
    V_PROC VARCHAR2(30);
  V_ERRMSG VARCHAR2(300);
  V_FOUND  BOOLEAN;
  V_USER   VARCHAR2(100) := UPPER(NVL(V('APP_USER'),USER));
  -- INITIALIZE PACKAGE VARIABLES
  VX           VARCHAR2(1) := ONLINE_MODEL_PKG.PKG_ALLOW_DELETE;
  PK_CHG_ERROR EXCEPTION;
  UPD_ERROR    EXCEPTION;
  DEL_ERROR    EXCEPTION;
  BEGIN
    V_PROC                                                           := 'ELEMENT_MODELS_BIUDR_TRG';
    V_ERRMSG                                                         := NULL;
    IF ( INSERTING OR (UPDATING AND ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'Y') ) THEN
      :new.ELEMENT_NAME                                              := SUBSTR(UPPER(TRIM(:new.ELEMENT_NAME)),1,60);
      /* ==========================================================================================================
      LOGIC TO POPULATE COLUMN LCLS_ELEMENTS_ELEMENT_ID:
      LCLS_ELEMENTS_ELEMENT_ID IS A FK REFERENCING ELEMENT_ID IN TABLE LCLS_INFRASTRUCTURE.LCLS_ELEMENTS.
      IT IS POPULATED *ONLY* IF ELEMENT_NAME IS FOUND IN LCLS_INFRASTRUCTURE.LCLS_ELEMENTS TABLE.
      IF ELEMENT_NAME IS *NOT* FOUND IN LCLS_INFRASTRUCTURE.LCLS_ELEMENTS TABLE OR IF ELEMENT_NAME IS NULL,
      THEN LCLS_ELEMENTS_ELEMENT_ID IS NULL AND ELEMENT_NAME REMAINS AS IS.
      NOTE: DURING THE CHECK TO SEE IF ELEMENT_NAME IS FOUND IN LCLS_INFRASTRUCTURE.LCLS_ELEMENTS, TWO CHECKS ARE DONE.
      IF ELEMENT_NAME IS NOT FOUND IN THE FIRST CHECK, THEN THE RIGHTMOST CHARACTER OF ELEMENT_NAME IS REMOVED
      AND A SECOND CHECK IS MADE.  IF THIS FAILS ALSO, THEN THE ELEMENT_NAME IS CONSIDERED TO BE "NOT FOUND".
      SO THEN:
      NULL OUT :new.LCLS_ELEMENTS_ELEMENT_ID REGARDLESS OF WHETHER OR NOT IT IS NULL.
      IF :new.ELEMENT_NAME IS NULL, THEN I'M DONE.
      IF :new.ELEMENT_NAME IS NOT NULL, THEN VALIDATE IT'S EXISTENCE IN TABLE LCLS_INFRASTRUCTURE.LCLS_ELEMENTS,
      DOING TWO CHECKS AS EXPLAINED ABOVE IN THE "NOTE".
      IF FOUND, THEN POPULATE COLUMN LCLS_ELEMENTS_ELEMENT_ID WITH THE ASSOCIATED ELEMENT_ID.
      IF NOT FOUND, THEN LEAVE COLUMN LCLS_ELEMENTS_ELEMENT_ID NULL AND LEAVE COLUMN ELEMENT_NAME AS IS.
      ========================================================================================================== */
      :new.LCLS_ELEMENTS_ELEMENT_ID := NULL;
      IF ( :new.ELEMENT_NAME        IS NOT NULL ) THEN
        BEGIN
          SELECT ELEMENT_ID
          INTO :new.LCLS_ELEMENTS_ELEMENT_ID
          FROM LCLS_INFRASTRUCTURE.LCLS_ELEMENTS
          WHERE ELEMENT = :new.ELEMENT_NAME;
          V_FOUND      := TRUE;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_FOUND := FALSE;
        END;
        -----------------
        IF ( NOT V_FOUND ) THEN
          BEGIN
            SELECT ELEMENT_ID
            INTO :new.LCLS_ELEMENTS_ELEMENT_ID
            FROM LCLS_INFRASTRUCTURE.LCLS_ELEMENTS
            WHERE ELEMENT = SUBSTR(:new.ELEMENT_NAME,1,LENGTH(:new.ELEMENT_NAME)-1);
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
            NULL;
          END;
        END IF; -- IF ( NOT V_FOUND ) THEN
      ELSIF ( :new.ELEMENT_NAME IS NULL ) THEN
        NULL;
      END IF; -- IF ( :new.ELEMENT_NAME IS NOT NULL ) THEN
    END IF;   -- IF ( INSERTING OR (UPDATING AND ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'Y') ) THEN
    -----------------
    IF INSERTING THEN
      IF ( :new.ID IS NULL ) THEN
        SELECT ELEMENT_MODELS_SEQ.NEXTVAL INTO :new.ID FROM DUAL;
      END IF;
      IF ( :new.CREATED_BY IS NULL ) THEN
        :new.CREATED_BY    := V_USER;
      END IF;
      IF ( :new.DATE_CREATED IS NULL ) THEN
        :new.DATE_CREATED    := SYSDATE;
      END IF;
      :new.UPDATED_BY   := NULL;
      :new.DATE_UPDATED := NULL;
    ELSIF UPDATING THEN
      IF ( ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'Y' ) THEN
        IF (:new.ID                         IS NOT NULL AND :new.ID <> :old.ID ) THEN
          V_ERRMSG                          := 'ERROR: '||V_PROC||': ATTEMPTING TO CHANGE PRIMARY KEY '||TO_CHAR(:old.ID)||' DURING UPDATE OF ELEMENT_MODELS.';
          RAISE PK_CHG_ERROR;
        END IF;
        :new.UPDATED_BY                        := V_USER;
        :new.DATE_UPDATED                      := SYSDATE;
      ELSIF ( ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'N' ) THEN
        V_ERRMSG                               := 'ERROR ('||V_PROC||'): UPDATE OPERATION NOT PERMITTED FOR TABLE ELEMENT_MODELS.';
        RAISE UPD_ERROR;
      END IF;
    ELSIF DELETING THEN
      IF ( ONLINE_MODEL_PKG.PKG_ALLOW_DELETE = 'Y' ) THEN
        NULL;
      ELSIF ( ONLINE_MODEL_PKG.PKG_ALLOW_DELETE = 'N' ) THEN
        V_ERRMSG                               := 'ERROR ('||V_PROC||'): DELETE OPERATION NOT PERMITTED FOR TABLE ELEMENT_MODELS.';
        RAISE DEL_ERROR;
      END IF;
    END IF; -- IF INSERTING THEN
    -- COMMIT;
  EXCEPTION
  WHEN PK_CHG_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20010, V_ERRMSG);
  WHEN UPD_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20020, V_ERRMSG);
  WHEN DEL_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20030, V_ERRMSG);
  WHEN OTHERS THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    V_ERRMSG                          := SUBSTR('ERROR: '||V_PROC||': '||SQLERRM,1,300);
    RAISE_APPLICATION_ERROR(-20040, V_ERRMSG);
  END;
  /
CREATE OR REPLACE TRIGGER MACHINE_MODEL.GOLD_BIUDR_TRG BEFORE
  INSERT OR
  UPDATE OR
  DELETE ON MACHINE_MODEL.GOLD FOR EACH ROW DECLARE
    -- PRAGMA AUTONOMOUS_TRANSACTION;
    V_PROC VARCHAR2(30);
  V_ERRMSG VARCHAR2(300);
  V_USER   VARCHAR2(100) := UPPER(NVL(V('APP_USER'),USER));
  -- INITIALIZE PACKAGE VARIABLES
  VX           VARCHAR2(1) := ONLINE_MODEL_PKG.PKG_ALLOW_DELETE;
  PK_CHG_ERROR EXCEPTION;
  UPD_ERROR    EXCEPTION;
  DEL_ERROR    EXCEPTION;
  BEGIN
    /* FYI: NO MORE THAN ONE RUNS ROW MAY BE "Gold" PER GIVEN COMBINATION OF RUN_SOURCE_CHK (DESIGN OR EXTANT) AND MODEL_MODES_ID (5,51,52,53). */
    V_PROC   := 'GOLD_BIUDR_TRG';
    V_ERRMSG := NULL;
    IF INSERTING OR UPDATING THEN
      :new.COMMENTS := SUBSTR(TRIM(:new.COMMENTS),1,200);
    END IF;
    IF INSERTING THEN
      IF ( :new.ID IS NULL ) THEN
        SELECT GOLD_SEQ.NEXTVAL INTO :new.ID FROM DUAL;
      END IF;
      IF ( :new.CREATED_BY IS NULL ) THEN
        :new.CREATED_BY    := V_USER;
      END IF;
      IF ( :new.DATE_CREATED IS NULL ) THEN
        :new.DATE_CREATED    := SYSDATE;
      END IF;
      :new.UPDATED_BY   := NULL;
      :new.DATE_UPDATED := NULL;
    ELSIF UPDATING THEN
      IF ( ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'Y' ) THEN
        IF (:new.ID                         IS NOT NULL AND :new.ID <> :old.ID ) THEN
          V_ERRMSG                          := 'ERROR: '||V_PROC||': ATTEMPTING TO CHANGE PRIMARY KEY '||TO_CHAR(:old.ID)||' DURING UPDATE OF GOLD.';
          RAISE PK_CHG_ERROR;
        END IF;
        :new.UPDATED_BY                        := V_USER;
        :new.DATE_UPDATED                      := SYSDATE;
      ELSIF ( ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'N' ) THEN
        V_ERRMSG                               := 'ERROR ('||V_PROC||'): UPDATE OPERATION NOT PERMITTED FOR TABLE GOLD.';
        RAISE UPD_ERROR;
      END IF;
    ELSIF DELETING THEN
      IF ( ONLINE_MODEL_PKG.PKG_ALLOW_DELETE = 'Y' ) THEN
        NULL;
      ELSIF ( ONLINE_MODEL_PKG.PKG_ALLOW_DELETE = 'N' ) THEN
        V_ERRMSG                               := 'ERROR ('||V_PROC||'): DELETE OPERATION NOT PERMITTED FOR TABLE GOLD.';
        RAISE DEL_ERROR;
      END IF;
    END IF; -- IF INSERTING THEN
    -- COMMIT;
  EXCEPTION
  WHEN PK_CHG_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20010, V_ERRMSG);
  WHEN UPD_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20020, V_ERRMSG);
  WHEN DEL_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20030, V_ERRMSG);
  WHEN OTHERS THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    V_ERRMSG                          := SUBSTR('ERROR: '||V_PROC||': '||SQLERRM,1,300);
    RAISE_APPLICATION_ERROR(-20040, V_ERRMSG);
  END;
  /
CREATE OR REPLACE TRIGGER MACHINE_MODEL.MODEL_DEVICES_BIUDR_TRG BEFORE
  INSERT OR
  UPDATE OR
  DELETE ON MACHINE_MODEL.MODEL_DEVICES FOR EACH ROW DECLARE
    -- PRAGMA AUTONOMOUS_TRANSACTION;
    V_PROC VARCHAR2(30);
  V_ERRMSG VARCHAR2(300);
  V_USER   VARCHAR2(100) := UPPER(NVL(V('APP_USER'),USER));
  -- INITIALIZE PACKAGE VARIABLES
  VX           VARCHAR2(1) := ONLINE_MODEL_PKG.PKG_ALLOW_DELETE;
  PK_CHG_ERROR EXCEPTION;
  UPD_ERROR    EXCEPTION;
  DEL_ERROR    EXCEPTION;
  BEGIN
    V_PROC   := 'MODEL_DEVICES_BIUDR_TRG';
    V_ERRMSG := NULL;
    IF INSERTING OR UPDATING THEN
      :new.DEVICE_PROPERTY := SUBSTR(UPPER(TRIM(:new.DEVICE_PROPERTY)),1,30);
    END IF;
    IF INSERTING THEN
      IF ( :new.ID IS NULL ) THEN
        SELECT MODEL_DEVICES_SEQ.NEXTVAL INTO :new.ID FROM DUAL;
      END IF;
      IF ( :new.CREATED_BY IS NULL ) THEN
        :new.CREATED_BY    := V_USER;
      END IF;
      IF ( :new.DATE_CREATED IS NULL ) THEN
        :new.DATE_CREATED    := SYSDATE;
      END IF;
      :new.UPDATED_BY   := NULL;
      :new.DATE_UPDATED := NULL;
    ELSIF UPDATING THEN
      IF ( ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'Y' ) THEN
        IF (:new.ID                         IS NOT NULL AND :new.ID <> :old.ID ) THEN
          V_ERRMSG                          := 'ERROR: '||V_PROC||': ATTEMPTING TO CHANGE PRIMARY KEY '||TO_CHAR(:old.ID)||' DURING UPDATE OF MODEL_DEVICES.';
          RAISE PK_CHG_ERROR;
        END IF;
        :new.UPDATED_BY                        := V_USER;
        :new.DATE_UPDATED                      := SYSDATE;
      ELSIF ( ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'N' ) THEN
        V_ERRMSG                               := 'ERROR ('||V_PROC||'): UPDATE OPERATION NOT PERMITTED FOR TABLE MODEL_DEVICES.';
        RAISE UPD_ERROR;
      END IF;
    ELSIF DELETING THEN
      IF ( ONLINE_MODEL_PKG.PKG_ALLOW_DELETE = 'Y' ) THEN
        NULL;
      ELSIF ( ONLINE_MODEL_PKG.PKG_ALLOW_DELETE = 'N' ) THEN
        V_ERRMSG                               := 'ERROR ('||V_PROC||'): DELETE OPERATION NOT PERMITTED FOR TABLE MODEL_DEVICES.';
        RAISE DEL_ERROR;
      END IF;
    END IF; -- IF INSERTING THEN
    -- COMMIT;
  EXCEPTION
  WHEN PK_CHG_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20010, V_ERRMSG);
  WHEN UPD_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20020, V_ERRMSG);
  WHEN DEL_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20030, V_ERRMSG);
  WHEN OTHERS THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    V_ERRMSG                          := SUBSTR('ERROR: '||V_PROC||': '||SQLERRM,1,300);
    RAISE_APPLICATION_ERROR(-20040, V_ERRMSG);
  END;
  /
CREATE OR REPLACE TRIGGER MACHINE_MODEL.MODEL_LINES_BIUDR_TRG BEFORE
  INSERT OR
  UPDATE OR
  DELETE ON MACHINE_MODEL.MODEL_LINES FOR EACH ROW DECLARE
    -- PRAGMA AUTONOMOUS_TRANSACTION;
    V_PROC VARCHAR2(30);
  V_ERRMSG VARCHAR2(300);
  V_USER   VARCHAR2(100) := UPPER(NVL(V('APP_USER'),USER));
  -- INITIALIZE PACKAGE VARIABLES
  -- VX              VARCHAR2(1) := ONLINE_MODEL_PKG.PKG_ALLOW_DELETE;
  PK_CHG_ERROR EXCEPTION;
  DEL_ERROR    EXCEPTION;
  BEGIN
    V_PROC   := 'MODEL_LINES_BIUDR_TRG';
    V_ERRMSG := NULL;
    IF INSERTING OR UPDATING THEN
      :new.MODEL_LINE_NAME := SUBSTR(TRIM(:new.MODEL_LINE_NAME),1,60);
    END IF;
    IF INSERTING THEN
      -- NOTE: A SEQUENCE IS NOT USED HERE TO POPULATE THE PRIMARY KEY.
      --       THE PRIMARY KEY IS DELIBERATELY POPULATED MANUALLY VIA SQL OR SOME GUI INTERFACE.
      IF ( :new.CREATED_BY IS NULL ) THEN
        :new.CREATED_BY    := V_USER;
      END IF;
      IF ( :new.DATE_CREATED IS NULL ) THEN
        :new.DATE_CREATED    := SYSDATE;
      END IF;
      :new.UPDATED_BY   := NULL;
      :new.DATE_UPDATED := NULL;
    ELSIF UPDATING THEN
      IF (:new.ID IS NOT NULL AND :new.ID <> :old.ID ) THEN
        V_ERRMSG  := 'ERROR: '||V_PROC||': ATTEMPTING TO CHANGE PRIMARY KEY '||TO_CHAR(:old.ID)||' DURING UPDATE OF MODEL_LINES.';
        RAISE PK_CHG_ERROR;
      END IF;
      :new.UPDATED_BY   := V_USER;
      :new.DATE_UPDATED := SYSDATE;
    ELSIF DELETING THEN
      -- UPDATE CHILD TABLE ROWS; DELETION OF CHILD ROWS NOT ALLOWED.
      BEGIN
        UPDATE MODEL_MODES SET MODEL_LINES_ID = NULL WHERE MODEL_LINES_ID = :old.ID;
      EXCEPTION
      WHEN OTHERS THEN
        V_ERRMSG := SUBSTR('ERROR ('||V_PROC||'): UPDATE MODEL_MODES SET MODEL_LINES_ID TO NULL DURING DELETE FROM MODEL_LINES FOR ID='|| TO_CHAR(:old.ID)||' => '||SQLERRM,1,300);
        RAISE DEL_ERROR;
      END;
    END IF; -- IF INSERTING THEN
    -- COMMIT;
  EXCEPTION
  WHEN PK_CHG_ERROR THEN
    RAISE_APPLICATION_ERROR(-20010, V_ERRMSG);
  WHEN DEL_ERROR THEN
    RAISE_APPLICATION_ERROR(-20020, V_ERRMSG);
  WHEN OTHERS THEN
    V_ERRMSG := SUBSTR('ERROR: '||V_PROC||': '||SQLERRM,1,300);
    RAISE_APPLICATION_ERROR(-20030, V_ERRMSG);
  END;
  /
CREATE OR REPLACE TRIGGER MACHINE_MODEL.MODEL_MODES_BIUDR_TRG BEFORE
  INSERT OR
  UPDATE OR
  DELETE ON MACHINE_MODEL.MODEL_MODES FOR EACH ROW DECLARE
    -- PRAGMA AUTONOMOUS_TRANSACTION;
    V_PROC VARCHAR2(30);
  V_ERRMSG VARCHAR2(300);
  V_USER   VARCHAR2(100) := UPPER(NVL(V('APP_USER'),USER));
  -- INITIALIZE PACKAGE VARIABLES
  -- VX              VARCHAR2(1) := ONLINE_MODEL_PKG.PKG_ALLOW_DELETE;
  PK_CHG_ERROR EXCEPTION;
  DEL_ERROR    EXCEPTION;
  BEGIN
    V_PROC   := 'MODEL_MODES_BIUDR_TRG';
    V_ERRMSG := NULL;
    IF INSERTING OR UPDATING THEN
      NULL;
    END IF;
    IF INSERTING THEN
      -- NOTE: A SEQUENCE IS NOT USED HERE TO POPULATE THE PRIMARY KEY.
      --       THE PRIMARY KEY IS DELIBERATELY POPULATED MANUALLY VIA SQL OR SOME GUI INTERFACE.
      IF ( :new.CREATED_BY IS NULL ) THEN
        :new.CREATED_BY    := V_USER;
      END IF;
      IF ( :new.DATE_CREATED IS NULL ) THEN
        :new.DATE_CREATED    := SYSDATE;
      END IF;
      :new.UPDATED_BY   := NULL;
      :new.DATE_UPDATED := NULL;
    ELSIF UPDATING THEN
      IF (:new.ID IS NOT NULL AND :new.ID <> :old.ID ) THEN
        V_ERRMSG  := 'ERROR: '||V_PROC||': ATTEMPTING TO CHANGE PRIMARY KEY '||TO_CHAR(:old.ID)||' DURING UPDATE OF MODEL_MODES.';
        RAISE PK_CHG_ERROR;
      END IF;
      :new.UPDATED_BY   := V_USER;
      :new.DATE_UPDATED := SYSDATE;
    ELSIF DELETING THEN
      -- UPDATE CHILD TABLE ROWS; DELETION OF CHILD ROWS NOT ALLOWED.
      BEGIN
        UPDATE RUNS SET MODEL_MODES_ID = NULL WHERE MODEL_MODES_ID = :old.ID;
      EXCEPTION
      WHEN OTHERS THEN
        V_ERRMSG := SUBSTR('ERROR ('||V_PROC||'): UPDATE RUNS SET MODEL_MODES_ID TO NULL DURING DELETE FROM MODEL_MODES FOR ID='|| TO_CHAR(:old.ID)||' => '||SQLERRM,1,300);
        RAISE DEL_ERROR;
      END;
    END IF; -- IF INSERTING THEN
    -- COMMIT;
  EXCEPTION
  WHEN PK_CHG_ERROR THEN
    RAISE_APPLICATION_ERROR(-20010, V_ERRMSG);
  WHEN DEL_ERROR THEN
    RAISE_APPLICATION_ERROR(-20020, V_ERRMSG);
  WHEN OTHERS THEN
    V_ERRMSG := SUBSTR('ERROR: '||V_PROC||': '||SQLERRM,1,300);
    RAISE_APPLICATION_ERROR(-20030, V_ERRMSG);
  END;
  /
CREATE OR REPLACE TRIGGER MACHINE_MODEL.RUNS_BIUDR_TRG BEFORE
  INSERT OR
  UPDATE OR
  DELETE ON MACHINE_MODEL.RUNS FOR EACH ROW DECLARE
    -- PRAGMA AUTONOMOUS_TRANSACTION;
    V_PROC VARCHAR2(30);
  V_ERRMSG VARCHAR2(300);
  V_USER   VARCHAR2(100) := UPPER(NVL(V('APP_USER'),USER));
  -- INITIALIZE PACKAGE VARIABLES
  VX             VARCHAR2(1) := ONLINE_MODEL_PKG.PKG_ALLOW_DELETE;
  PK_CHG_ERROR   EXCEPTION;
  UPD_ERROR      EXCEPTION;
  DEL_ERROR      EXCEPTION;
  DEL_MDEV_ERROR EXCEPTION;
  DEL_ELEM_ERROR EXCEPTION;
  DEL_GOLD_ERROR EXCEPTION;
  BEGIN
    V_PROC   := 'RUNS_BIUDR_TRG';
    V_ERRMSG := NULL;
    IF INSERTING OR UPDATING THEN
      :new.RUN_SOURCE_CHK       := SUBSTR(UPPER(TRIM(:new.RUN_SOURCE_CHK)),1,60);
      :new.RUN_ELEMENT_FILENAME := SUBSTR(TRIM(:new.RUN_ELEMENT_FILENAME),1,60);
      :new.RUN_DEVICE_FILENAME  := SUBSTR(TRIM(:new.RUN_DEVICE_FILENAME),1,60);
      :new.COMMENTS             := SUBSTR(TRIM(:new.COMMENTS),1,200);
    END IF;
    IF INSERTING THEN
      IF ( :new.ID IS NULL ) THEN
        SELECT RUNS_SEQ.NEXTVAL INTO :new.ID FROM DUAL;
      END IF;
      IF ( :new.CREATED_BY IS NULL ) THEN
        :new.CREATED_BY    := V_USER;
      END IF;
      IF ( :new.DATE_CREATED IS NULL ) THEN
        :new.DATE_CREATED    := SYSDATE;
      END IF;
      :new.UPDATED_BY            := NULL;
      :new.DATE_UPDATED          := NULL;
      IF ( :new.RUN_ELEMENT_DATE IS NULL ) THEN
        :new.RUN_ELEMENT_DATE    := :new.DATE_CREATED;
      END IF;
      IF ( :new.RUN_DEVICE_DATE IS NULL ) THEN
        :new.RUN_DEVICE_DATE    := :new.DATE_CREATED;
      END IF;
    ELSIF UPDATING THEN
      IF ( ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'Y' ) THEN
        IF (:new.ID                         IS NOT NULL AND :new.ID <> :old.ID ) THEN
          V_ERRMSG                          := 'ERROR: '||V_PROC||': ATTEMPTING TO CHANGE PRIMARY KEY '||TO_CHAR(:old.ID)||' DURING UPDATE OF RUNS.';
          RAISE PK_CHG_ERROR;
        END IF;
        :new.UPDATED_BY            := V_USER;
        :new.DATE_UPDATED          := SYSDATE;
        IF ( :new.RUN_ELEMENT_DATE IS NULL ) THEN
          :new.RUN_ELEMENT_DATE    := SYSDATE;
        END IF;
        IF ( :new.RUN_DEVICE_DATE IS NULL ) THEN
          :new.RUN_DEVICE_DATE    := SYSDATE;
        END IF;
      ELSIF ( ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE = 'N' ) THEN
        V_ERRMSG                               := 'ERROR ('||V_PROC||'): UPDATE OPERATION NOT PERMITTED FOR TABLE RUNS.';
        RAISE UPD_ERROR;
      END IF;
    ELSIF DELETING THEN
      IF ( ONLINE_MODEL_PKG.PKG_ALLOW_DELETE = 'Y' ) THEN
        -- DELETE CHILD TABLE ROWS
        BEGIN
          DELETE FROM MACHINE_MODEL.MODEL_DEVICES WHERE RUNS_ID = :old.ID;
        EXCEPTION
        WHEN OTHERS THEN
          V_ERRMSG := SUBSTR('ERROR ('||V_PROC||'): DELETE FROM MODEL_DEVICES DURING DELETE FROM RUNS FOR ID='|| TO_CHAR(:old.ID)||' => '||SQLERRM,1,300);
          RAISE DEL_MDEV_ERROR;
        END;
        -----------
        BEGIN
          DELETE FROM MACHINE_MODEL.ELEMENT_MODELS WHERE RUNS_ID = :old.ID;
        EXCEPTION
        WHEN OTHERS THEN
          V_ERRMSG := SUBSTR('ERROR ('||V_PROC||'): DELETE FROM ELEMENT_MODELS DURING DELETE FROM RUNS FOR ID='|| TO_CHAR(:old.ID)||' => '||SQLERRM,1,300);
          RAISE DEL_ELEM_ERROR;
        END;
        -----------
        BEGIN
          DELETE FROM MACHINE_MODEL.GOLD WHERE RUNS_ID = :old.ID;
        EXCEPTION
        WHEN OTHERS THEN
          V_ERRMSG := SUBSTR('ERROR ('||V_PROC||'): DELETE FROM GOLD DURING DELETE FROM RUNS FOR ID='|| TO_CHAR(:old.ID)||' => '||SQLERRM,1,300);
          RAISE DEL_GOLD_ERROR;
        END;
      ELSIF ( ONLINE_MODEL_PKG.PKG_ALLOW_DELETE = 'N' ) THEN
        V_ERRMSG                               := 'ERROR ('||V_PROC||'): DELETE OPERATION NOT PERMITTED FOR TABLE RUNS.';
        RAISE DEL_ERROR;
      END IF;
    END IF; -- IF INSERTING THEN
    -- COMMIT;
  EXCEPTION
  WHEN PK_CHG_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20010, V_ERRMSG);
  WHEN UPD_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20020, V_ERRMSG);
  WHEN DEL_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20030, V_ERRMSG);
  WHEN DEL_MDEV_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20040, V_ERRMSG);
  WHEN DEL_ELEM_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20050, V_ERRMSG);
  WHEN DEL_GOLD_ERROR THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    RAISE_APPLICATION_ERROR(-20060, V_ERRMSG);
  WHEN OTHERS THEN
    ONLINE_MODEL_PKG.PKG_ALLOW_UPDATE := 'N';
    ONLINE_MODEL_PKG.PKG_ALLOW_DELETE := 'N';
    V_ERRMSG                          := SUBSTR('ERROR: '||V_PROC||': '||SQLERRM,1,300);
    RAISE_APPLICATION_ERROR(-20070, V_ERRMSG);
  END;
  /