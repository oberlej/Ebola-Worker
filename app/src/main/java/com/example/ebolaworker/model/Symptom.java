package com.example.ebolaworker.model;

import java.util.List;

/**
 * Created by jeremiaoberle on 11/14/16.
 */

public class Symptom {
    public long id;
    public String label;
    public int is_present;
    public long patient_id;

    public Symptom(long id, long patient_id,String label,int is_present) {
        super();
        this.patient_id=patient_id;
        this.label=label;
        this.is_present = is_present;
        this.id = id;
    }
}
