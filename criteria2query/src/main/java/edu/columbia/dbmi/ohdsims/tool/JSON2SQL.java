package edu.columbia.dbmi.ohdsims.tool;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.columbia.dbmi.ohdsims.pojo.GenerateSqlRequest;
import edu.columbia.dbmi.ohdsims.pojo.GenerateSqlResult;
import edu.columbia.dbmi.ohdsims.pojo.SourceStatement;
import edu.columbia.dbmi.ohdsims.pojo.TranslatedStatement;
import org.apache.commons.lang3.StringUtils;
import org.ohdsi.circe.cohortdefinition.CohortExpressionQueryBuilder;
import org.ohdsi.sql.SqlRender;
import org.ohdsi.sql.SqlTranslate;

import java.util.Collections;
import java.util.Map;

public class JSON2SQL {
    private static final CohortExpressionQueryBuilder queryBuilder = new CohortExpressionQueryBuilder();
    private static final String DEFAULT_DIALECT = "sql server";
    private static final String TEMP_DATABASE_SCHEMA_PLACEHOLDER = "@temp_database_schema";

    /**
     * Generate SQL template from json
     * This part of code is modified from org.ohdsi.webapi.service.CohortDefinitionService.java
     */
    public static String SQLTemplate(String expression) {

        // map json to java object
        ObjectMapper objectMapper = new ObjectMapper();
        GenerateSqlRequest request = new GenerateSqlRequest();
        try{
            request = objectMapper.readValue(expression, GenerateSqlRequest.class);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        // build the SqlTemplate
        CohortExpressionQueryBuilder.BuildExpressionQueryOptions options = request.options;
        GenerateSqlResult result = new GenerateSqlResult();
        if (options == null)
        {
            options = new CohortExpressionQueryBuilder.BuildExpressionQueryOptions();
        }
        String expressionSql = queryBuilder.buildExpressionQuery(request.expression, options);
        result.templateSql = SqlRender.renderSql(expressionSql, null, null);
        return result.templateSql;
    }

    /**
     * SQL Template -> PostgresSQL
     * this part of code is modified from org.ohdsi.webapi.service.SqlRenderService.java
     */
    public static String template2Postgres(String sqljson) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        SourceStatement sourceStatement = new SourceStatement();
        try {
            sourceStatement = objectMapper.readValue(sqljson.toString(), SourceStatement.class);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        TranslatedStatement translatedStatement = sqlToPostgresSql(sourceStatement);
        return translatedStatement.getTargetSQL();
    }

    /**
     * helper function: translate to postgresSql
     * this part of code is modified from org.ohdsi.webapi.service.SqlRenderService.java
     */
    private static TranslatedStatement sqlToPostgresSql(SourceStatement sourceStatement) {
        TranslatedStatement translated = new TranslatedStatement();
        if(sourceStatement == null) {
            return translated;
        }
        sourceStatement.setOracleTempSchema(TEMP_DATABASE_SCHEMA_PLACEHOLDER);
        try {
            Map<?, ?> parameters = sourceStatement.getParameters() == null ? Collections.emptyMap() : sourceStatement.getParameters();

            String renderedSQL = SqlRender.renderSql(
                    sourceStatement.getSql(),
                    parameters.keySet().toArray(new String[0]),
                    parameters.values().toArray(new String[0])
            );

            translated.setTargetSQL(translateSql(sourceStatement, renderedSQL));

            return translated;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * helper function: translate to postgresSql
     * this part of code is modified from org.ohdsi.webapi.service.SqlRenderService.java
     */
    private static String translateSql(SourceStatement sourceStatement, String renderedSQL) {
        if (StringUtils.isEmpty(sourceStatement.getTargetDialect()) || DEFAULT_DIALECT.equals(sourceStatement.getTargetDialect())) {
            return renderedSQL;
        }
        return SqlTranslate.translateSql(renderedSQL, sourceStatement.getTargetDialect(), SqlTranslate.generateSessionId(), sourceStatement.getOracleTempSchema());
    }

    public static String fixAge(String sqlScript, String dialect){
        if (dialect.equals("PostgreSQL")){
            sqlScript = sqlScript.replaceAll("EXTRACT\\(YEAR FROM E.start_date\\) - P.year_of_birth", "date_part\\('year',AGE\\(current_date, make_date\\(P.year_of_birth,P.month_of_birth, P.day_of_birth\\)\\)\\)");
        }else if(dialect.equals("MSSQL_Server"))
        sqlScript = sqlScript.replaceAll("YEAR\\(E.start_date\\) - P.year_of_birth", "FLOOR\\(DATEDIFF\\(DAY, DATEFROMPARTS\\(P.year_of_birth,P.month_of_birth, P.day_of_birth\\), GETDATE\\(\\)\\) / 365.25\\)");
        return sqlScript;
    }


}
