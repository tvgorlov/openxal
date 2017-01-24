ALTER TABLE "MACHINE_MODEL"."MODEL_DEVICES" 
    DROP CONSTRAINT CKC_MODEL_DEVICES_DEV_PROP ;
ALTER TABLE "MACHINE_MODEL"."MODEL_DEVICES"
    ADD CONSTRAINT CKC_MODEL_DEVICES_DEV_PROP CHECK ( "DEVICE_PROPERTY" IS NULL OR ( "DEVICE_PROPERTY" IN ('B','BACT','BDES','A','P','ADES','PDES','APRX','MISX','MISY','MISZ','ROTX','ROTY','ROTZ','ENBL') )) ;
