/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

import java.util.List;
import templates.input.WebFlowParser;

/**
 *
 * @author Gusta
 */
public class Bag {

    public String id;
    String bag;
    String userName;
    List<Webflow> flows;

    public Bag(String nam, List<Webflow> fls) {
        flows = fls;
        id = nam;
        bag = nam;
        int i;
        if (nam.contains(".fls")) {
            i = nam.indexOf(".fls");
        } else {
            i = nam.length();
        }
        userName = nam.substring(nam.indexOf(WebFlowParser.mark) + WebFlowParser.mark.length(), i);
    }

    public String getRelationalRepresentation() {
        // + relace mezi flows
        StringBuilder res = new StringBuilder("user(" + userName + "), ");
        for (Webflow f1 : flows) {
            for (Webflow f2 : flows) {
                if (f1.timestamp < f2.timestamp) {
                    res.append("after(").append(f1.hash).append(",").append(f2.hash).append("), ");
                    //System.out.println(res.length());
                }
            }
        }
        return res.toString();
    }
}
