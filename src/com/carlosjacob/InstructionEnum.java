package com.carlosjacob;

    public enum InstructionEnum {
        PUSHI(0),
        PUSHM(1),
        POPM(2),
        STDOUT(3),
        STDIN(4),
        ADD(5),
        SUB(6),
        MUL(7),
        DIV(8),
        GRT(9),
        LES(10),
        EQU(11),
        NEQ(12),
        GEQ(13),
        LEQ(14),
        JUMPZ(15),
        JUMP(16),
        LABEL(17);

        private int id;

        InstructionEnum(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
