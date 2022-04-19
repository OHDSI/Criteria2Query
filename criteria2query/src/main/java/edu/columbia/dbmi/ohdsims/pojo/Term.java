package edu.columbia.dbmi.ohdsims.pojo;

import edu.columbia.dbmi.ohdsims.util.JSONUtil;
import org.apache.xpath.operations.Bool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Term implements Cloneable {
    private Integer termId; //Store Term ID; terms with same text but different positions have different termID
    private Integer vocabularyId; //Store ConceptSet ID
    private String text;
    private boolean neg;
    private String categorey;
    private Integer start_index;
    private Integer end_index;
    private HashMap<Integer, String> relations;
    private Integer conceptId; //Store Concept ID
    private String conceptName;
    private List<Integer> index; //Store the position of the term in the sentence


    public Integer getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(Integer vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategorey() {
        return categorey;
    }

    public void setCategorey(String categorey) {
        this.categorey = categorey;
    }

    public Integer getStart_index() {
        return start_index;
    }

    public void setStart_index(Integer start_index) {
        this.start_index = start_index;
    }

    public Integer getEnd_index() {
        return end_index;
    }

    public void setEnd_index(Integer end_index) {
        this.end_index = end_index;
    }

    public String toString() {
        String tostr = "<" + this.termId.toString() + ">[" + this.categorey + "]:" + this.text;
        return tostr;
    }

    public boolean isNeg() {
        return neg;
    }

    public void setNeg(boolean neg) {
        this.neg = neg;
    }

    public HashMap<Integer, String> getRelations() {
        return relations;
    }

    public void setRelations(HashMap<Integer, String> relations) {
        this.relations = relations;
    }

    public Integer getConceptId() {
        return conceptId;
    }

    public void setConceptId(Integer conceptId) {
        this.conceptId = conceptId;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public List<Integer> getIndex() {
        return index;
    }

    public void setIndex(List<Integer> index) {
        this.index = index;
    }


    //shallow clone
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean partialEquals(Object o) {
        Term term = (Term) o;
        Boolean match = false;
        if (this.getTermId().equals(term.getTermId()) &&
                this.getText().equals(term.getText()) &&
                this.getCategorey().equals(term.getCategorey()) &&
                this.getStart_index().equals(term.getStart_index()) &&
                this.getEnd_index().equals(term.getEnd_index())) {
            if (Arrays.asList(GlobalSetting.conceptSetDomains).contains(this.getCategorey())) {
                if (this.getConceptId().equals(term.getConceptId())) {
                    match = true;
                }
            } else {
                match = true;
            }
        }
        return match;
    }



}
