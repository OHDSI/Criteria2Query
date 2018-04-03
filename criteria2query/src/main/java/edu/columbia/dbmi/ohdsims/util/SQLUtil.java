package edu.columbia.dbmi.ohdsims.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLUtil {
	 final static String url="jdbc:postgresql://45.76.6.224:5432/synpuf1000";
	 final static String user="postgres";
	 final static String password = "postgres";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	public static String cleanSQL(String possql){
		String sql=possql.replace("@cdm_database_schema", "public");
        sql=sql.replace("@target_database_schema", "public");
        sql=sql.replace("@target_cohort_table", "cohort");
        sql=sql.replace("@target_cohort_id", "1");
        int x=sql.indexOf("DELETE FROM");
        System.out.println("--->"+x);
        sql=sql.substring(0, x);
        return sql;
	}
	public static Integer executeSQL(String possql){
		Connection connection=null;
        Statement statement =null;
        String count="0";
        try{
            Class.forName("org.postgresql.Driver");
            connection= DriverManager.getConnection(url, user, password);
            System.out.println("是否成功连接pg数据库"+connection);
            
            String sql=possql.replace("@cdm_database_schema", "public");
            sql=sql.replace("@target_database_schema", "public");
            sql=sql.replace("@target_cohort_table", "cohort");
            sql=sql.replace("@target_cohort_id", "1");
            int x=sql.indexOf("DELETE FROM");
            System.out.println("--->"+x);
            sql=sql.substring(0, x);
            //sql=sql+"select * from cohort_ends;";
            System.out.println(sql);
            //String sql="select count(*) from drug_exposure";
            statement=connection.createStatement(); 
            statement.executeUpdate(sql);
            sql="select count(*) from cohort_ends;";
            ResultSet resultSet= statement.executeQuery(sql);
          
            while(resultSet.next()){
                String name=resultSet.getString(1);
                System.out.println(name);
                count=name;
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            try{
                statement.close();
            }
            catch(SQLException e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }finally{
                try{
                    connection.close();
                }
                catch(SQLException e){
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
		return Integer.valueOf(count);
	}

}
