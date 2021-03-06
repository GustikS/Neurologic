/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lrnn.construction.template.specialPredicates;

/**
 *
 * @author Gusta
 */
public class NotEqualPredicate extends SpecialPredicate {

    public NotEqualPredicate(String iname) {
        name = iname;
    }

    @Override
    public double evaluate(String arg) {
        String[] args = arg.split(",");
        if (args.length == 2) {
            if (args[0].equals("VAR") || args[0].equals("VAR")) {
                return 1.0;
            }
            return args[0].equals(args[1]) ? 0 : 1;
        } else {
            return 0;
        }
    }

    @Override
    public void update(String args, double gradient) {
        //nothing to update here
    }
}
