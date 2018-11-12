# Criteria2Query
[In Development] An application to parse freetext inclusion criteria and produce a structured cohort definition that can be executed against OMOP CDM

[![Criteria2Query instruction](https://img.youtube.com/vi/EYN2Md-DCR8/0.jpg)](https://www.youtube.com/watch?v=EYN2Md-DCR8)


## Information Extraction

### Name Entity Recognition
We implemented our NER methods based on a sequence labeling method, Condition Random Fields (CRF), in CoreNLP with an empirical feature set. After NER, all entities were extracted from free-text criteria with predicted categories assigned automatically.

||Category|Definition|Examples|
|| ------------- |:-------------:| -----:|
| Entity	|Condition	|Conditions are records of a Person suggesting the presence of a disease or medical condition stated as a diagnosis, a sign or a symptom.	|Type 2 diabetes mellitus, Alzheimer’s disease.|
|| ------------- |:-------------:| -----:|
||Drug	|Drugs are biochemical substances formulated in such ways that when administered to a Person it will exert a certain physiological effect. |Acetaminophen,
Furosemide|


### Relation Extraction
Our pipeline implements binary relation extraction with two relationships: has_temp (temporal) and has_value (Table 2). Relations between entities are determined by reachability according to enhanced++ English universal dependency parsing results.

### Logic Detection
We developed a logic detection step following the information extraction pipeline to resolve the logic operators connecting clinical entities. Our heuristic method uses the conjunct tags in enhanced English universal dependency parsing results to group the entities and decompose the logic relations between entities and groups.

## Query Formulation


### [Criteria2Query Chat Room](https://gitter.im/Criteria2query/Lobby#)
