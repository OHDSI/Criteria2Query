# Criteria2Query  
<img src="/pictures/website.png"  width="800"/>

*Criteria2Query 2.0 is published!*
[Online Demo](http://34.70.212.14:8080/criteria2query_test/)

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
* [SynPUF_1K](http://www.ltscomputingllc.com/downloads/) and [SynPUF_5%](https://www.ohdsi.org/data-standardization/) datasets in CDM Version 5.2.2 format.
* OMOP CDM Vocabulary version 5 files. These can be obtained from [Athena](https://athena.ohdsi.org/search-terms/start).
* These are for the demonstration of real-time cohort SQL query execution (not strictly required)



Getting Started
=======
1. Download and install everything based on the system requirements above.

2. Git clone this repository.

3. Download the [negation scope detection model](https://drive.google.com/file/d/1uBbSL0_Zp70Z4vMlAQq43qlRhmg5cIq0/view?usp=sharing) and move it to the folder `NegationDetection`.

4. Create a virtual environment in Python and install packages based on `venv_requirements.txt`. (Instruction: https://packaging.python.org/en/latest/guides/installing-using-pip-and-virtual-environments/)

5. Change the directories of Negation Detection and the Python virtual environment in the file `/criteria2query/src/main/java/edu/columbia/dbmi/ohdsims/pojo/GlobalSetting.java` 
```
//Change the directories (examples)
public final static String negateDetectionFolder = "/opt/tomcat/NegationDetection";
public final static String virtualEnvFolder = "/opt/tomcat/python_virtualenvs/C2Q_NEGATION/bin"; // or "D:\\C2Q\\python_virtualenvs\\C2Q_NEGATION\\Scripts";
```

6. Import SynPUF_1K and SynPUF_5% datasets to your PostgreSQL DBMS (You can skip this step if they are already imported.)
    * Download the [SynPUF_1K](http://www.ltscomputingllc.com/downloads/) and [SynPUF_5%](https://www.ohdsi.org/data-standardization/) datasets in CDM Version 5.2.2 format.
    * Download the OMOP CDM Vocabulary version 5 files from [Athena](https://athena.ohdsi.org/search-terms/start).
    * Follow the instruction here (https://github.com/OHDSI/CommonDataModel/tree/v5.2.2/PostgreSQL) to create your instantiations of the Common Data Model for SynPUF_1K and SynPUF_5%, respectively. 

7. Connect to your own database (SynPUF_1K and SynPUF_5%) by changing the URL, user, and password in the file `/criteria2query/src/main/java/edu/columbia/dbmi/ohdsims/pojo/GlobalSetting.java` 
```
//Connect to the databases
    public final static String databaseURL1K = "jdbc:postgresql://localhost/synpuf1k";
    public final static String databaseURL5pct = "jdbc:postgresql://localhost/synpuf5pct";
    public final static String databaseUser = "Please connect to a database.";
    public final static String databasePassword = "*****";
```
8. Deploy C2Q and visit it in your web browser.



Publications
======
Fang, Y., Idnay, B., Sun, Y., Liu, H., Chen, Z., Marder, K., Xu, H., Schnall, R., & Weng, C. (2022). Combining human and machine intelligence for clinical trial eligibility querying. Journal of the American Medical Informatics Association : JAMIA, ocac051. Advance online publication. https://doi.org/10.1093/jamia/ocac051

Yuan, C., Ryan, P. B., Ta, C., Guo, Y., Li, Z., Hardin, J., Makadia, R., Jin, P., Shang, N., Kang, T., & Weng, C. (2019). Criteria2Query: a natural language interface to clinical databases for cohort definition. Journal of the American Medical Informatics Association : JAMIA, 26(4), 294–305. https://doi.org/10.1093/jamia/ocy178




Support
=======
If you have any questions/comments/feedback, please submit a [form](https://forms.gle/gQxnsrmsuJCmrn4R8) here or contact Dr. Chunhua Weng at Columbia University.
 
 