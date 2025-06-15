# Criteria2Query  
<img src="/pictures/website.png"  width="800"/>

*Criteria2Query 2.4 is published!*
<!-- [Online Demo](http://34.70.212.14:8080/criteria2query_test/) -->

Introduction
 ========
Criteria2Query (C2Q) is an automatic cohort identification system. It enhances human-computer collaboration to convert complex eligibility criteria text into more accurate and feasible cohort SQL queries. It synergizes machine efficiency and human intelligence of domain experts to enable real-time user intervention for criteria selection and simplification, parsing error correction, and context-dependent concept mapping.

 ## Features
 * An editable user interface with functions to prioritize or simplify the eligibility criteria text for cohort querying; 
 * Accessible and portable cohort SQL query formulation based on the Observational Medical Outcomes Partnership (OMOP) Common Data Model (CDM) version 5;
 * Real-time cohort query execution with result visualization. 

## Interface and use case example 
<img src="/pictures/example.png" width="800"/>



 System Requirements
 =======
* Java 8+
* Apache Maven 3
* Apache Tomcat
* Python 3.7.6+
* PostgreSQL DBMS (to demonstrate the real-time cohort SQL query execution, not strictly required)

Dependencies
========
* SynPUF_1K and SynPUF_5% datasets, or any other dataset in CDM Version 5.2.2 format.
* OMOP CDM Vocabulary version 5 files. These can be obtained from [Athena](https://athena.ohdsi.org/search-terms/start).
* [Usagi](https://github.com/OHDSI/Usagi) for concept mapping. 


Getting Started
=======
1. Install all required system dependencies as specified in the System Requirements section above.

2. Git clone this repository.

3. Download the [negation scope detection model](https://drive.google.com/file/d/1B2i_HC0CV0j5-7DSewiaA5Dbyr0IHOdk/view?usp=sharing) and place it in the `NegationDetection` directory.

4. Create a Python virtual environment and install the required packages from `venv_requirements.txt`. (Instruction: https://packaging.python.org/en/latest/guides/installing-using-pip-and-virtual-environments/)

5. Update the directory paths for Negation Detection and the Python virtual environment in the following file: `/criteria2query/src/main/java/edu/columbia/dbmi/ohdsims/pojo/GlobalSetting.java` 
```
// Set the directory for the negation detection model
public final static String negateDetectionFolder = "/opt/tomcat/NegationDetection";

// Set the path to the Python virtual environment
public final static String virtualEnvFolder = "/opt/tomcat/python_virtualenvs/C2Q_NEGATION/bin";
// For Windows, use the following format:
// public final static String virtualEnvFolder = "D:\\C2Q\\python_virtualenvs\\C2Q_NEGATION\\Scripts";
```

6. Download [Usagi](https://github.com/OHDSI/Usagi), and implement a POST API endpoint (referred to as the Concept Hub) that allows searching concepts by term and domain.

7. Configure the Concept Hub endpoint in the following file:`/criteria2query/src/main/java/edu/columbia/dbmi/ohdsims/pojo/GlobalSetting.java`
Update the concepthub URL to point to your POST API endpoint:
```
// Set the Concept Hub POST endpoint
public final static String concepthub = "http://localhost:8081/concepthub";
```

8. Import the SynPUF datasets into your PostgreSQL database
(Skip this step if the datasets are already imported.)
    * Download the SynPUF_1K and SynPUF_5% datasets in OMOP CDM version 5.2.2 format.
    * Download the OMOP CDM vocabulary files (v5) from [Athena](https://athena.ohdsi.org/search-terms/start).
    * Follow the instructions in the [OHDSI Common Data Model repository](https://github.com/OHDSI/CommonDataModel/tree/v5.2.2/PostgreSQL) to instantiate the CDM schema in your PostgreSQL database for both datasets.
    * You may also use your own datasets, as long as they conform to the OMOP CDM v5.2.2 format. 

9. Configure database connection settings
Update the database URLs, username, and password in the following file to connect to your SynPUF_1K and SynPUF_5% databases:`/criteria2query/src/main/java/edu/columbia/dbmi/ohdsims/pojo/GlobalSetting.java` 
```
//Connect to the databases
    public final static String databaseURL1K = "jdbc:postgresql://localhost/synpuf1k";
    public final static String databaseURL5pct = "jdbc:postgresql://localhost/synpuf5pct";
    public final static String databaseUser = "Please connect to a database.";
    public final static String databasePassword = "*****";
```
10. Deploy the C2Q application. Once configured, deploy the application and open it in your web browser.



Publications
======
Fang, Y., Idnay, B., Sun, Y., Liu, H., Chen, Z., Marder, K., Xu, H., Schnall, R., & Weng, C. (2022). Combining human and machine intelligence for clinical trial eligibility querying. Journal of the American Medical Informatics Association : JAMIA, ocac051. Advance online publication. https://doi.org/10.1093/jamia/ocac051

Yuan, C., Ryan, P. B., Ta, C., Guo, Y., Li, Z., Hardin, J., Makadia, R., Jin, P., Shang, N., Kang, T., & Weng, C. (2019). Criteria2Query: a natural language interface to clinical databases for cohort definition. Journal of the American Medical Informatics Association : JAMIA, 26(4), 294–305. https://doi.org/10.1093/jamia/ocy178




Support
=======
If you have any questions/comments/feedback, please submit a [form](https://forms.gle/gQxnsrmsuJCmrn4R8) here or contact Dr. Chunhua Weng at Columbia University.
 
 