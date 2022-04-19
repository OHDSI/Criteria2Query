package edu.columbia.dbmi.ohdsims.util;

import edu.columbia.dbmi.ohdsims.pojo.GlobalSetting;
import edu.columbia.dbmi.ohdsims.tool.JSON2SQL;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.json.CDL;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static edu.columbia.dbmi.ohdsims.pojo.GlobalSetting.ohdsi_api_base_url;


public class SQLUtil {
    final static String url1K = GlobalSetting.databaseURL1K;
    final static String url5pct = GlobalSetting.databaseURL5pct;
    final static String user = GlobalSetting.databaseUser;
    final static String password = GlobalSetting.databasePassword;

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        //Read the JSON file

        for(int i = 1; i<2; i++){
            String pathname = "./test cases/JSON/JSON"+ i+".txt";
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader);
            StringBuffer lineBuffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                lineBuffer.append(line);
            }
            String jsonTxt = new String(lineBuffer);
            JSONObject jsonObject = JSONObject.fromObject(jsonTxt);
            JSONObject expression = new JSONObject();
            expression.accumulate("expression", jsonObject);
            //System.out.println("expressionstr="+expression);

            //APIS in Online WebAPI
            //generate SQL template
            long startTime=System.currentTimeMillis();
            String results = HttpUtil.doPost(ohdsi_api_base_url + "cohortdefinition/sql", expression.toString());
            JSONObject resultjson = JSONObject.fromObject(results);
            //SQL template -> different SQLs
            JSONObject sqljson = new JSONObject();
            sqljson.accumulate("SQL", resultjson.get("templateSql"));
            sqljson.accumulate("targetdialect", "postgresql");
            results = HttpUtil.doPost(ohdsi_api_base_url + "sqlrender/translate", sqljson.toString());
            resultjson = JSONObject.fromObject(results);
            String sqlResult = (String) resultjson.get("targetSQL");
            //System.out.println(sqlResult);
            long endTime=System.currentTimeMillis();
            System.out.println("time(Online WebAPI): "+(endTime-startTime)+"ms");

            //APIs in local WebAPI by ZC
            //generate SQL template
            startTime=System.currentTimeMillis();
            String results1 = JSON2SQL.SQLTemplate(expression.toString());
            //SQL template -> different SQLs
            JSONObject sqljson1 = new JSONObject();
            sqljson1.accumulate("SQL", results1);
            sqljson1.accumulate("targetdialect", "postgresql");
            results1 = JSON2SQL.template2Postgres(sqljson1.toString());
            endTime=System.currentTimeMillis();
            System.out.println("time(Local WebAPI): "+(endTime-startTime)+"ms");
            String dataset = "SynPUF 1K dataset";
            executeSQL(results1, dataset);
        }



    }

    public static String cleanSQL(String possql) {
        String sql = possql.replace("@vocabulary_database_schema", "public");
        sql = sql.replace("@cdm_database_schema", "public");
        sql = sql.replace("@target_database_schema", "public");
        sql = sql.replace("@target_cohort_table", "cohort");
        sql = sql.replace("@target_cohort_id", "1");
        int x = sql.indexOf("DELETE FROM");
        //System.out.println("--->" + x);
        sql = sql.substring(0, x);
        return sql;
    }

    public static JSONArray executeSQL(String possql, String dataset) {
        Connection connection = null;
        Statement statement = null;
        JSONArray personArray = new JSONArray();
        if (user.equals("Please connect to a database.")){
            JSONObject person = new JSONObject();
            person.accumulate("person_id", "Please connect to a database. Right now, you can only download the JSON file and SQL script.");
            person.accumulate("birth_date", "*");
            person.accumulate("age", "*");
            person.accumulate("gender", "*");
            person.accumulate("race", "*");
            personArray.add(person);
            return personArray;
        }
        try {
            //connect to the database,
            Class.forName("org.postgresql.Driver");
            if(dataset.equals("SynPUF 1K dataset")){
                connection = DriverManager.getConnection(url1K, user, password);
            }else{
                connection = DriverManager.getConnection(url5pct, user, password);
            }

            System.out.println("succefully connect to the database" + connection);
            long startTime = System.currentTimeMillis();
            //Edit the name of the schemas and tables to match the ones which are used in the SQL script.
            String sql = possql.replace("@vocabulary_database_schema", "public");
            sql = sql.replace("@cdm_database_schema", "public");
            sql = sql.replace("@target_database_schema", "public");
            sql = sql.replace("@target_cohort_table", "cohort");
            sql = sql.replace("@target_cohort_id", "1");
            int x = sql.indexOf("DELETE FROM");
            sql = sql.substring(0, x);
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            long endTime = System.currentTimeMillis();
            System.out.println("sql1 execution time:"+(endTime - startTime) + "ms");
            startTime = System.currentTimeMillis();
            sql = "select person_id, INITCAP(c1.concept_name) as gender, birth_date, date_part('year',AGE(current_date, birth_date)), c2.concept_name as race\n" +
                    "from (select person_id, gender_concept_id, make_date(year_of_birth,month_of_birth, day_of_birth) as birth_date, race_concept_id\n" +
                    "from person \n" +
                    "where person_id in \n" +
                    "(select person_id from final_cohort))as a\n" +
                    "left join concept as c1 on c1.concept_id = a.gender_concept_id\n" +
                    "left join concept as c2 on c2.concept_id = a.race_concept_id\n" +
                    "order by person_id";

            ResultSet resultSet = statement.executeQuery(sql);
            endTime = System.currentTimeMillis();
            System.out.println("sql2 execution time:"+(endTime - startTime) + "ms");
            while (resultSet.next()) {
                JSONObject person = new JSONObject();
                String person_id = resultSet.getString(1);
                String gender = resultSet.getString(2);
                String birth_date = resultSet.getString(3);
                String age = resultSet.getString(4).split(" ")[0];
                String race = resultSet.getString(5);
                person.accumulate("person_id", person_id);
                person.accumulate("birth_date", birth_date);
                person.accumulate("age", age);
                person.accumulate("gender", gender);
                person.accumulate("race", race);
                personArray.add(person);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return personArray;
    }

}
