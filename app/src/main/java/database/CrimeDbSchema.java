package database;

public class CrimeDbSchema {

    //定义描述数据库的内部类CrimeTable
    public static final class CrimeTable{
        public static final String NAME = "crimes";

        //定义数据库字段
        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String PHONE = "phone";
        }
    }
}
