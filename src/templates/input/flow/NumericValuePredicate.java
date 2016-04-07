/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

/**
 *
 * @author Gusta
 */
public class NumericValuePredicate {

    public static String getRelational(String name, String val) {
        String res = "";
        String valu = val.substring(1, val.length());
        int value = Integer.parseInt(valu);
        switch (name) {
            case "bytes":
                if (value < 100) {
                    res += "small(" + val + ")";
                } else if (value < 1000) {
                    res += "medium(" + val + ")";
                } else {
                    res += "large(" + val + ")";
                }
                break;
            case "time":
                if (value < 100) {
                    res += "small(" + val + ")";
                } else if (value < 1000) {
                    res += "medium(" + val + ")";
                } else {
                    res += "large(" + val + ")";
                }
                break;
            default:
                throw new AssertionError();
        }
        return res;
    }

}
