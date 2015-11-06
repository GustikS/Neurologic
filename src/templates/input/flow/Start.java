/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Gustiik
 */
public class Start extends AbstractAttribute {

    private String stringStart;
    private Date dateStart;

    public Start(Date date) {
        this.stringStart = new SimpleDateFormat("HH-mm-ss").format(date);
        this.dateStart = date;
    }

    /**
     * @return the stringStart
     */
    public String getStringStart() {
        return stringStart;
    }

    /**
     * @param stringStart the stringStart to set
     */
    public void setStringStart(String stringStart) {
        this.stringStart = stringStart.trim();
    }

    /**
     * @return the dateStart
     */
    public Date getDateStart() {
        return dateStart;
    }

    /**
     * @param dateStart the dateStart to set
     */
    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }
    
        @Override
    public String toStringValue(){
        return stringStart;
    }
    
    @Override
    public int compareTo(AbstractAttribute att) {
        if (att.getClass() == getClass()){
            return Integer.MIN_VALUE;
        } else {
            Start st = (Start) att;
            return this.getDateStart().compareTo(st.getDateStart());
        }
    }
}
