package com.example.demo.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenJPAEntityMysql {

    private static String packageOutPath = "com.example.demo.lsq.db.entity";// 指定实体生成所在包的路径
    private static String authorName = "linshiq";// 作者名字

    // 数据库连接
    private static final String URL = "jdbc:mysql://localhost:3306/lsq";
    private static final String NAME = "root";
    private static final String PASS = "qqqq";
    private static final String DRIVER = "com.mysql.jdbc.Driver";

    // 指定表名,不指定则获取全部表名
    private static List<String> tabbeNames = new ArrayList<>();

    public static void main(String[] args) throws Exception {
       tabbeNames.add("test");
        GenJPAEntityMysql s = new GenJPAEntityMysql();
        s.process();
    }

    /**
     * 入口
     *
     * @throws Exception
     */
    public void process() throws Exception {
        Connection con = createConn();
        List<String> tablenames;
        if (tabbeNames == null || tabbeNames.size() == 0){
            tablenames = getAllTableName(con);
        } else {
            tablenames = tabbeNames;
        }
        Map<String, List<String>> tablenameKeyCommentsValueMap = getAllColumnComments(con, tablenames);
        Map<String, List<String>> tablenameKeyPriKeysValueMap = getPriKeyInfo(con, tablenames);
        createEntity(con, tablenames, tablenameKeyCommentsValueMap, tablenameKeyPriKeysValueMap);
    }

    private void createEntity(Connection con, List<String> tablenames, Map<String, List<String>> tablenameKeyCommentsValueMap, Map<String, List<String>> tablenameKeyPriKeysValueMap) throws Exception {
        for (String tablename : tablenames) {
            // 查要生成实体类的表
            String sql = "select * from " + tablename;

            PreparedStatement pStemt = con.prepareStatement(sql);
            ResultSetMetaData rsmd = pStemt.getMetaData();
            List<TableColumn> tableColumns = new ArrayList<>();
            int size = rsmd.getColumnCount(); // 统计列
            List<String> keyInfos = tablenameKeyPriKeysValueMap.get(tablename);
            for (int i = 1; i <= size; i++) {
                TableColumn tableColumn = new GenJPAEntityMysql.TableColumn();
                // 获取字段名称
                tableColumn.columnName = rsmd.getColumnName(i).toLowerCase();
                // 获取字段类型
                tableColumn.columnType = rsmd.getColumnTypeName(i);
                tableColumn.isNull = rsmd.isNullable(i) > 0;
                // 是否自动递增
                tableColumn.isAutoInctement = rsmd.isAutoIncrement(i);
                // 是否主键
                tableColumn.isPri = keyInfos != null && keyInfos.size() > 0 ? keyInfos.contains(tableColumn.columnName) : false;
                tableColumns.add(tableColumn);
            }

            String content = createSingleEntity(tablename, tableColumns, tablenameKeyCommentsValueMap.get(tablename));
            createJavaEntity(content, tablename);
        }
    }

    /**
     * 创建Entity
     *
     * @param content
     * @param tablename
     */
    private void createJavaEntity(String content, String tablename) {
        try {
            File directory = new File("");
            String path = this.getClass().getResource("").getPath();

            System.out.println(path);
            System.out.println("src/?/" + path.substring(path.lastIndexOf("/com/", path.length())));
            String[] dire = packageOutPath.split("\\.");
            String createPath = directory.getAbsolutePath() + "/src/main/java";
            // 循环生成目录
            for (int i = 0; i < dire.length; i++) {
                createPath = createPath + "/" + dire[i];
                // 判断文件是否存在
                File directoryExist = new File(createPath);
                // 不存在则创建一个目录出来
                if (!directoryExist.exists()) {
                    directoryExist.mkdir();
                }
            }

            // 使用驼峰命名法
            tablename = useHumpNomenclature(tablename);
            String outputPath = directory.getAbsolutePath() + "/src/main/java/"
                    + this.packageOutPath.replace(".", "/") + "/" + initcap(tablename) + ".java";
            FileWriter fw = new FileWriter(outputPath);
            System.out.println(outputPath);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createSingleEntity(String tablename, List<TableColumn> tableColumns, List<String> columnComments) throws Exception {

        boolean loadUtilPackage = false;
        boolean loadSQLPackage = false;
        boolean loadDecimalPackage = false;
        for (TableColumn tableColumn : tableColumns) {
            String colType = tableColumn.columnType.toLowerCase();
            if (colType.equalsIgnoreCase("datetime") || colType.equalsIgnoreCase("timestamp")) {
                loadUtilPackage = true;
            }
            if (colType.equalsIgnoreCase("image") || colType.equalsIgnoreCase("text")) {
                loadUtilPackage = true;
            }
            if (colType.contains("decimal")) {
                loadDecimalPackage = true;
            }
        }
        StringBuffer sb = new StringBuffer();

        sb.append("package " + packageOutPath + ";\r\n");
        sb.append("\r\n");
        sb.append("import javax.persistence.*;\r\n");
        // 判断是否导入工具包
        if (loadUtilPackage) {
            sb.append("import java.util.*;\r\n");
        }
        if (loadSQLPackage) {
            sb.append("import java.sql.*;\r\n");
        }
        if (loadDecimalPackage) {
            sb.append("import java.math.BigDecimal;\r\n");
        }

        // 使用驼峰命名法
        String tableName = useHumpNomenclature(tablename);
        // 注释部分
        sb.append("/**\r\n");
        sb.append("* " + tablename + " 实体类\r\n");
        sb.append("* " + new Date() + " " + authorName + "\r\n");
        sb.append("*/ \r\n");
        // 实体部分
        sb.append("@Entity\r\n"); // 补充Entity注解
        sb.append("@Table(name = \"" + tablename.toUpperCase() + "\")\r\n");// 补充Table注解
        sb.append("public class " + initcap(tableName) + "{\r\n");
        processAllAttrs(sb, columnComments, tableColumns);// 属性
        processAllMethod(sb, columnComments, tableColumns);// get set方法
        sb.append("}\r\n");

        // System.out.println(sb.toString());
        return sb.toString();
    }

    private Connection createConn() {
        // 创建连接
        Connection con = null;
        PreparedStatement pStemt = null;
        try {
            try {
                Class.forName(DRIVER);
            } catch (ClassNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            con = DriverManager.getConnection(URL, NAME, PASS);
        } catch (Exception e) {
            System.out.println("创建连接失败");
            throw new NullPointerException("创建连接失败");
        }
        return con;
    }

    /**
     * 获取所有表名
     *
     * @param con
     * @return
     * @throws Exception
     */
    private List<String> getAllTableName(Connection con) throws Exception {
        List<String> tablenames = new ArrayList<>();

        // 获取所有的表名
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
        while (rs.next()) {
            System.out.println("------------------------------");
            System.out.println("表名：" + rs.getString(3));
            System.out.println("表所属用户名：" + rs.getString(2));

            String tablename = rs.getString(3);
            tablenames.add(tablename);
        }
        return tablenames;
    }

    /**
     * 获取所有注释
     *
     * @param con
     * @param tablenames
     * @return
     * @throws Exception
     */
    private Map getAllColumnComments(Connection con, List<String> tablenames) throws Exception {
        Map<String, List<String>> tablenameKeyCommentsValueMap = new HashMap<>();
        for (String tablename : tablenames) {
            // 获取表中字段的所有注释
            PreparedStatement pStemt1 = con.prepareStatement("SELECT * FROM " + tablename);
            ResultSet commentRs = pStemt1.executeQuery("show full columns from " + tablename);
            List<String> columnComments = new ArrayList<>();// 列名注释集合
            while (commentRs.next()) {
                columnComments.add(commentRs.getString("Comment"));
            }
            tablenameKeyCommentsValueMap.put(tablename, columnComments);
        }
        return tablenameKeyCommentsValueMap;
    }

    /**
     * 获取所有主键信息
     *
     * @param con
     * @param tablenames
     * @return
     * @throws Exception
     */
    private Map getPriKeyInfo(Connection con, List<String> tablenames) throws Exception {
        Map<String, List<String>> tablenameKeyPriKeysValueMap = new HashMap<>();
        // 获取所有的主键
        // oracle 语句 select
        // table_name,dbms_metadata.get_ddl('TABLE','TABLE_NAME')from
        // dual,user_tables where table_name='TABLE_NAME';
        // TABLE_NAME为具体的表名,要求大写
        for (String tablename : tablenames) {
            String sql_table = "SHOW CREATE TABLE " + tablename;
            PreparedStatement pre = con.prepareStatement(sql_table);
            ResultSet rs1 = pre.executeQuery();
            if (rs1.next()) {

                // 正则匹配数据
                Pattern pattern = Pattern.compile("PRIMARY KEY \\(\\`(.*)\\`\\)");
                Matcher matcher = pattern.matcher(rs1.getString(2));
                matcher.find();
                String data = "";
                try {
                    data = matcher.group();
                } catch (IllegalStateException e) {
                    System.out.println("没主键");
                    continue;
                }

                // 过滤对于字符
                data = data.replaceAll("\\`|PRIMARY KEY \\(|\\)", "");
                // 拆分字符
                String[] stringArr = data.split(",");
                System.out.println("主键为:" + Arrays.toString(stringArr));
                tablenameKeyPriKeysValueMap.put(tablename, Arrays.asList(stringArr));
            }
        }
        return tablenameKeyPriKeysValueMap;
    }

    /**
     * 功能：生成所有属性
     *
     * @param sb
     */
    private void processAllAttrs(StringBuffer sb, List<String> columnComments, List<TableColumn> tableColumns) {

        for (int i = 0; i < tableColumns.size(); i++) {
            sb.append("\r\n");
            TableColumn tableColumn = tableColumns.get(i);
            String colname = tableColumn.columnName;
            String type = tableColumn.columnType;
            if (tableColumn.isPri){
                sb.append("\t@Id\r\n");
            }
            if (tableColumn.isAutoInctement){
                sb.append("\t@GeneratedValue(strategy = GenerationType.IDENTITY)\r\n");
            }
            sb.append("\t@Column(name = \"" + colname.toUpperCase() + "\"");
            if (!tableColumn.isNull){
                sb.append(", nullable=false");
            }else{
                sb.append(", nullable=true");
            }
            sb.append(")\r\n");
            // 使用驼峰命名法
            String[] attributesArr = colname.split("_");
            if (attributesArr.length > 1) {
                StringBuffer sbAttributes = new StringBuffer();
                sbAttributes.append(attributesArr[0]);
                for (int j = 1; j < attributesArr.length; j++) {
                    sbAttributes.append(initcap(attributesArr[j]));
                }
                colname = sbAttributes.toString();
            }

            sb.append("\tprivate " + sqlType2JavaType(type) + " " + colname + ";\r\n");
        }

    }

    /**
     * 功能：生成所有方法
     *
     * @param sb
     * @param columnComments
     */
    private void processAllMethod(StringBuffer sb, List<String> columnComments, List<TableColumn> tableColumns) {

        /**
         * fiche no 卡片号
         */
        for (int i = 0; i < tableColumns.size(); i++) {

            String colname = tableColumns.get(i).columnName;
            String colType = tableColumns.get(i).columnType;
            // 使用驼峰命名法
            String[] attributesArr = colname.split("_");
            String colName = colname;
            if (attributesArr.length > 1) {
                StringBuffer sbAttributes = new StringBuffer();
                sbAttributes.append(attributesArr[0]);
                for (int j = 1; j < attributesArr.length; j++) {
                    sbAttributes.append(initcap(attributesArr[j]));
                }
                colname = sbAttributes.toString();
            }


            sb.append("\t/**").append("\r\n");// 注释部分
            sb.append("\t  * " + colName).append("\r\n");// 注释部分
            sb.append("\t  * " + columnComments.get(i)).append("\r\n");// 注释部分
            sb.append("\t  */").append("\r\n");// 注释部分

            sb.append("\tpublic void set" + initcap(colname) + "(" + sqlType2JavaType(colType) + " " + colname
                    + "){\r\n");
            sb.append("\t\tthis." + colname + "=" + colname + ";\r\n");
            sb.append("\t}\r\n");

            sb.append("\t/**").append("\r\n");// 注释部分
            sb.append("\t  * " + colName).append("\r\n");// 注释部分
            sb.append("\t  * " + columnComments.get(i)).append("\r\n");// 注释部分
            sb.append("\t  */").append("\r\n");// 注释部分
            sb.append("\tpublic " + sqlType2JavaType(colType) + " get" + initcap(colname) + "(){\r\n");
            sb.append("\t\treturn " + colname + ";\r\n");
            sb.append("\t}\r\n");
        }

    }

    /**
     * 功能：将输入字符串的首字母改成大写
     *
     * @param str
     * @return
     */
    private String initcap(String str) {

        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 功能：获得列的数据类型
     *
     * @param sqlType
     * @return
     */
    private String sqlType2JavaType(String sqlType) {
        // System.out.println(sqlType);
        if (sqlType.equalsIgnoreCase("bit")) {
            return "boolean";
        } else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "byte";
        } else if (sqlType.equalsIgnoreCase("smallint")) {
            return "short";
        } else if (sqlType.equalsIgnoreCase("int")) {
            return "int";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "float";
        } else if (sqlType.equalsIgnoreCase("numeric") || sqlType.equalsIgnoreCase("real")) {
            return "double";
        } else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "BigDecimal";
        } else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
                || sqlType.equalsIgnoreCase("text")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("timestamp")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("image")) {
            return "Blod";
        } else {

            String clone = sqlType.toLowerCase();

            if (clone.contains("decimal")) {
                return "BigDecimal";
            }

            return null;
        }

    }

    /**
     * @param str
     * @return
     * @Author linshiqin
     * <p>
     * <li>2019年5月29日-下午4:12:22</li>
     * <li>功能说明：将字段修改为驼峰命名法</li>
     * </p>
     */
    private String useHumpNomenclature(String str) {

        String[] strArr = str.split("_");

        if (strArr.length > 1) {
            StringBuffer sb = new StringBuffer();
            sb.append(strArr[0]);
            for (int j = 1; j < strArr.length; j++) {
                sb.append(initcap(strArr[j]));
            }
            return sb.toString();
        }

        return str;
    }

    public static class TableColumn {

        public String columnName;// 字段名称
        public String columnType;// 字段类型
        public boolean isPri;// 是否主键
        public boolean isNull;// 是否为空
        public boolean isAutoInctement; //是否自动递增
    }
}
