package com.example.ebolaworker.model;

/**
 * Created by jeremiaoberle on 11/14/16.
 */

public class BasicPatient {
    public long id;
    public String first_name;
    public String last_name;
    public String birth_date;

    public BasicPatient(long id, String last_name, String first_name, String birth_date) {
        super();
        this.first_name = first_name;
        this.last_name = last_name;
        this.birth_date = birth_date;
        this.id = id;
    }
}
