# Criteria2Query
[![](https://img.shields.io/badge/online--demo-available-brightgreen.svg)](http://www.ohdsi.org/web/criteria2query/)
[![](https://img.shields.io/badge/introduction--video-available-brightgreen.svg)](https://www.youtube.com/watch?v=EYN2Md-DCR8)
[![](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Criteria2query/Lobby#)

C2Q 2.0 will be published here shortly. Please hold until we update this page with C2Q 2.0 before April 15th, 2022. 


### [Criteria2Query Online Demo](http://www.ohdsi.org/web/criteria2query/)

### Criteria2Query Introduction
[![Criteria2Query instruction](https://i.imgur.com/CshfWd4.png)](https://www.youtube.com/watch?v=EYN2Md-DCR8)


## Information Extraction

### Name Entity Recognition
We implemented our NER methods based on a sequence labeling method, Condition Random Fields (CRF), in CoreNLP with an empirical feature set. After NER, all entities were extracted from free-text criteria with predicted categories assigned automatically.


### Relation Extraction
Our pipeline implements binary relation extraction with two relationships: has_temp (temporal) and has_value (Table 2). Relations between entities are determined by reachability according to enhanced++ English universal dependency parsing results.

### Logic Detection
We developed a logic detection step following the information extraction pipeline to resolve the logic operators connecting clinical entities. Our heuristic method uses the conjunct tags in enhanced English universal dependency parsing results to group the entities and decompose the logic relations between entities and groups.

## Query Formulation

### Entity Normalization
We wrapped a lucene-based OMOP mapping tool called Usagi as a web service that queries entity terms and their domains to map terms to OMOP standard concepts. Using OHDSI APIs , we leverage the rich hierarchical relations among concepts in the OMOP CDM to include all descendants for condition concepts and all drugs sharing the same ingredient for drug concepts.

### Logic Translation
We developed a logic translation component in Criteria2Query to translate logic within structured criteria to the target data model. In cohort definitions in the OMOP CDM, the logic relations of “And” and “Or” are represented by the templates “have all of the following criteria” and “have any of the following criteria”, respectively. Exclusion criteria are represented by “with exactly 0 using all occurrences”. 

### Attribute Normalization
We adapted a library for recognizing and normalizing time expressions, SUTime, to standardized temporal expressions into TIMEX3 format first. We then use regular expressions to transform temporal information in TIMEX3 format into the target CDM format. We also developed a heuristic method for the numeric normalization using regular expressions to fill the results in the target format. Both temporal and numeric attributes are linked to their related criteria based on relation extraction results.

### [Criteria2Query Chat Room](https://gitter.im/Criteria2query/Lobby#)

If you have any question, please contact Dr. Chunhua Weng at Columbia University.