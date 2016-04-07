/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

import templates.input.StringParser;

/**
 *
 * @author Gusta
 */
public class Domain {

    public static int idd = 0;
    public String id;
    public String url;
    String dns;

    public Domain(String name) {
        id = "dom" + idd++;
        url = name;
    }

    public String getRelationalRepresentation() {
        String res = "domain(" + id + "," + url + "), ";
        res += "hasURL(" + id + "," + id + "-URL), ";
        StringParser sp = new StringParser();
        res += sp.string2Structure("next", url, id + "-URL");
        return res;
    }
}
